const API_BASE = 'http://localhost:8080';

const loginForm = document.getElementById('loginForm');
const loginView = document.getElementById('login-view');
const appContainer = document.getElementById('app-container');
const displayUser = document.getElementById('displayUser');
const logoutBtn = document.getElementById('logoutBtn');

const loginCard = document.getElementById('login-card');
const registerCard = document.getElementById('register-card');
const showRegister = document.getElementById('showRegister');
const showLoginLink = document.getElementById('showLogin');
const registerForm = document.getElementById('registerForm');
const loginError = document.getElementById('loginError');

const menuItems = document.querySelectorAll('.menu-item');
const pageTitle = document.getElementById('page-title');
const searchInput = null;
const btnNuevo = document.getElementById('btnNuevo');

const statProductos = document.getElementById('statProductos');
const statBodegas = document.getElementById('statBodegas');
const statMovimientos = document.getElementById('statMovimientos');
const statAuditorias = document.getElementById('statAuditorias');

const inicioStockBajo = document.getElementById('inicioStockBajo');
const inicioMovimientosRecientes = document.getElementById('inicioMovimientosRecientes');

const bodegasCountText = document.getElementById('bodegasCountText');
const productosCountText = document.getElementById('productosCountText');
const auditoriasCountText = document.getElementById('auditoriasCountText');

const bodegasTableBody = document.getElementById('bodegasTableBody');
const productosTableBody = document.getElementById('productosTableBody');
const auditoriaTableBody = document.getElementById('auditoriaTableBody');
const movimientosList = document.getElementById('movimientosList');

const productoFormBox = document.getElementById('productoFormBox');
const productoForm = document.getElementById('productoForm');
const productoFormError = document.getElementById('productoFormError');
const productoBodegaId = document.getElementById('productoBodegaId');
const cancelProductoBtn = document.getElementById('cancelProductoBtn');
const cancelProductoBtnBottom = document.getElementById('cancelProductoBtnBottom');

const bodegaFormBox = document.getElementById('bodegaFormBox');
const bodegaForm = document.getElementById('bodegaForm');
const bodegaFormError = document.getElementById('bodegaFormError');
const bodegaEncargadoId = document.getElementById('bodegaEncargadoId');
const cancelBodegaBtn = document.getElementById('cancelBodegaBtn');
const cancelBodegaBtnBottom = document.getElementById('cancelBodegaBtnBottom');

const movimientoFormBox = document.getElementById('movimientoFormBox');
const movimientoForm = document.getElementById('movimientoForm');
const movimientoFormError = document.getElementById('movimientoFormError');
const movTipo = document.getElementById('movTipo');
const movUsuarioId = document.getElementById('movUsuarioId');
const movBodegaOrigenId = document.getElementById('movBodegaOrigenId');
const movBodegaDestinoId = document.getElementById('movBodegaDestinoId');
const movProductoId = document.getElementById('movProductoId');
const movCantidad = document.getElementById('movCantidad');
const movOrigenWrap = document.getElementById('movOrigenWrap');
const movDestinoWrap = document.getElementById('movDestinoWrap');
const cancelMovimientoBtn = document.getElementById('cancelMovimientoBtn');
const cancelMovimientoBtnBottom = document.getElementById('cancelMovimientoBtnBottom');

const auditoriaFilterForm = document.getElementById('auditoriaFilterForm');
const auditoriaIdFiltro = document.getElementById('auditoriaIdFiltro');
const auditoriaUsuarioFiltro = document.getElementById('auditoriaUsuarioFiltro');
const auditoriaOperacionFiltro = document.getElementById('auditoriaOperacionFiltro');
const auditoriaFilterError = document.getElementById('auditoriaFilterError');
const clearAuditoriaFiltersBtn = document.getElementById('clearAuditoriaFiltersBtn');

let currentSection = 'inicio';

const titles = {
    inicio: 'Inicio',
    bodegas: 'Gestión de Bodegas',
    productos: 'Gestión de Productos',
    movimientos: 'Movimientos de Inventario',
    auditoria: 'Auditoría de Cambios'
};

function getToken() {
    return localStorage.getItem('token');
}

function getCurrentUser() {
    return localStorage.getItem('currentUser') || 'Empleado';
}

function setSession(token, username) {
    localStorage.setItem('token', token);
    localStorage.setItem('currentUser', username);
}

function clearSession() {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
}

function mostrarErrorLogin(mensaje) {
    if (!loginError) return;
    loginError.textContent = mensaje;
    loginError.classList.remove('d-none');
}

function limpiarErrorLogin() {
    if (!loginError) return;
    loginError.textContent = '';
    loginError.classList.add('d-none');
}

function setFormError(element, mensaje) {
    if (!element) return;
    element.textContent = mensaje;
    element.classList.remove('hidden');
}

function clearFormError(element) {
    if (!element) return;
    element.textContent = '';
    element.classList.add('hidden');
}

async function apiFetch(endpoint, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };

    const token = getToken();
    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE}${endpoint}`, {
        ...options,
        headers
    });

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    let data = null;

    if (text) {
        try {
            data = JSON.parse(text);
        } catch {
            data = text;
        }
    }

    if (!response.ok) {
        throw new Error(extractErrorMessage(data) || 'Error en la petición');
    }

    return data;
}

function extractErrorMessage(data) {
    if (!data) return null;
    if (typeof data === 'string') return data;
    if (data.message) return data.message;
    if (data.errors) return Object.values(data.errors).join('\n');
    return null;
}

function formatPrice(value) {
    return Number(value || 0).toLocaleString('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    });
}

function formatDate(value) {
    if (!value) return 'Sin fecha';
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return value;
    return date.toLocaleString('es-CO');
}

function getProductStatus(stock) {
    if (stock <= 0) return { text: 'Agotado', className: 'status-out' };
    if (stock < 10) return { text: 'Stock bajo', className: 'status-low' };
    return { text: 'Disponible', className: 'status-ok' };
}

function getMovimientoClass(tipoMovimiento) {
    if (tipoMovimiento === 'ENTRADA') return 'border-warning';
    if (tipoMovimiento === 'SALIDA') return 'border-danger';
    return 'border-primary';
}

function buildMovimientoText(movimiento) {
    const detalles = (movimiento.detalles || [])
        .map(detalle => `${detalle.cantidad} unidades de ${detalle.nombreProducto}`)
        .join(', ');

    if (movimiento.tipoMovimiento === 'ENTRADA') {
        return `${detalles || 'Movimiento registrado'} - Bodega destino: ${movimiento.bodegaDestino?.nombre || 'No disponible'}`;
    }

    if (movimiento.tipoMovimiento === 'SALIDA') {
        return `${detalles || 'Movimiento registrado'} - Bodega origen: ${movimiento.bodegaOrigen?.nombre || 'No disponible'}`;
    }

    return `${detalles || 'Movimiento registrado'} - De ${movimiento.bodegaOrigen?.nombre || 'No disponible'} a ${movimiento.bodegaDestino?.nombre || 'No disponible'}`;
}

function renderStockBajo(productos) {
    if (!inicioStockBajo) return;

    if (!productos || productos.length === 0) {
        inicioStockBajo.innerHTML = '<div class="text-muted">No hay productos con stock bajo.</div>';
        return;
    }

    inicioStockBajo.innerHTML = productos.map(producto => `
        <div class="p-3 mb-2 bg-light border-start border-warning border-4">
            <strong>${producto.nombre}</strong>
            <div class="small text-muted">Stock: ${producto.stock} | Bodega: ${producto.bodega?.nombre || 'Sin bodega'}</div>
        </div>
    `).join('');
}

function renderMovimientosRecientes(movimientos) {
    if (!inicioMovimientosRecientes) return;

    if (!movimientos || movimientos.length === 0) {
        inicioMovimientosRecientes.innerHTML = '<div class="text-muted">No hay movimientos registrados.</div>';
        return;
    }

    inicioMovimientosRecientes.innerHTML = movimientos.map(movimiento => `
        <div class="p-3 mb-2 bg-light border-start ${getMovimientoClass(movimiento.tipoMovimiento)} border-4">
            <strong>${movimiento.tipoMovimiento}</strong>
            <div class="small">${buildMovimientoText(movimiento)}</div>
            <div class="small text-muted mt-1">${formatDate(movimiento.fecha)}</div>
        </div>
    `).join('');
}

function showApp(username) {
    displayUser.innerText = username || 'Empleado';
    loginView.style.opacity = '0';
    loginView.style.transition = '0.5s';

    setTimeout(() => {
        loginView.classList.add('hidden');
        appContainer.classList.remove('hidden');
    }, 300);
}

function showLogin() {
    loginView.classList.remove('hidden');
    appContainer.classList.add('hidden');
    loginView.style.opacity = '1';
    displayUser.innerText = 'Empleado';
    pageTitle.innerText = 'Inicio';
    ocultarTodosLosFormularios();
}

function ocultarTodosLosFormularios() {
    if (productoFormBox) productoFormBox.classList.add('hidden');
    if (bodegaFormBox) bodegaFormBox.classList.add('hidden');
    if (movimientoFormBox) movimientoFormBox.classList.add('hidden');

    if (productoForm) productoForm.reset();
    if (bodegaForm) bodegaForm.reset();
    if (movimientoForm) movimientoForm.reset();

    clearFormError(productoFormError);
    clearFormError(bodegaFormError);
    clearFormError(movimientoFormError);
}

async function cargarDashboard() {
    try {
        const [bodegas, productos, movimientos, auditorias, stockBajo] = await Promise.all([
            apiFetch('/api/bodega/public'),
            apiFetch('/api/producto/public'),
            apiFetch('/api/movimiento/public'),
            apiFetch('/api/auditoria/public'),
            apiFetch('/api/producto/stock-bajo')
        ]);

        if (statBodegas) statBodegas.innerText = bodegas.length;
        if (statProductos) statProductos.innerText = productos.length;
        if (statMovimientos) statMovimientos.innerText = movimientos.length;
        if (statAuditorias) statAuditorias.innerText = auditorias.length;

        const movimientosOrdenados = [...movimientos]
            .sort((a, b) => new Date(b.fecha) - new Date(a.fecha))
            .slice(0, 5);

        renderStockBajo(stockBajo);
        renderMovimientosRecientes(movimientosOrdenados);
    } catch (error) {
        if (inicioStockBajo) inicioStockBajo.innerHTML = `<div class="text-danger">${error.message}</div>`;
        if (inicioMovimientosRecientes) inicioMovimientosRecientes.innerHTML = `<div class="text-danger">${error.message}</div>`;
    }
}

async function cargarBodegas() {
    try {
        const bodegas = await apiFetch('/api/bodega/public');
        if (bodegasCountText) bodegasCountText.innerText = `Mostrando: ${bodegas.length} registros`;

        if (bodegasTableBody) {
            bodegasTableBody.innerHTML = bodegas.map(bodega => `
                <tr>
                    <td>${bodega.id}</td>
                    <td><strong>${bodega.nombre}</strong></td>
                    <td>${bodega.ubicacion}</td>
                    <td>${bodega.capacidad}</td>
                    <td>${bodega.nombreEncargado || 'Sin encargado'}</td>
                </tr>
            `).join('');

            if (bodegas.length === 0) {
                bodegasTableBody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No hay bodegas registradas</td></tr>';
            }
        }
    } catch (error) {
        if (bodegasTableBody) {
            bodegasTableBody.innerHTML = `<tr><td colspan="5" class="text-center text-danger">${error.message}</td></tr>`;
        }
    }
}

async function cargarProductos() {
    try {
        const productos = await apiFetch('/api/producto/public');
        if (productosCountText) productosCountText.innerText = `Mostrando: ${productos.length} registros`;

        if (productosTableBody) {
            productosTableBody.innerHTML = productos.map(producto => {
                const status = getProductStatus(producto.stock);
                return `
                    <tr>
                        <td>${producto.id}</td>
                        <td><strong>${producto.nombre}</strong></td>
                        <td>${producto.categoria || 'Sin categoría'}</td>
                        <td>${producto.stock}</td>
                        <td>${formatPrice(producto.precio)}</td>
                        <td><span class="status-badge ${status.className}">${status.text}</span></td>
                    </tr>
                `;
            }).join('');

            if (productos.length === 0) {
                productosTableBody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No hay productos registrados</td></tr>';
            }
        }
    } catch (error) {
        if (productosTableBody) {
            productosTableBody.innerHTML = `<tr><td colspan="6" class="text-center text-danger">${error.message}</td></tr>`;
        }
    }
}

async function cargarMovimientos() {
    try {
        const movimientos = await apiFetch('/api/movimiento/public');

        if (movimientosList) {
            movimientosList.innerHTML = movimientos.map(movimiento => `
                <div class="p-3 mb-2 bg-light border-start ${getMovimientoClass(movimiento.tipoMovimiento)} border-4">
                    <strong>${movimiento.tipoMovimiento}:</strong> ${buildMovimientoText(movimiento)}
                    <div class="text-muted small mt-1">${formatDate(movimiento.fecha)} | Usuario: ${movimiento.nombreUsuario || 'Sin usuario'}</div>
                </div>
            `).join('');

            if (movimientos.length === 0) {
                movimientosList.innerHTML = '<div class="text-center text-muted py-3">No hay movimientos registrados</div>';
            }
        }
    } catch (error) {
        if (movimientosList) {
            movimientosList.innerHTML = `<div class="text-center text-danger py-3">${error.message}</div>`;
        }
    }
}

function renderAuditoriaRows(auditorias) {
    if (!auditoriaTableBody) return;

    if (auditorias.length === 0) {
        auditoriaTableBody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No hay auditorías registradas</td></tr>';
        return;
    }

    auditoriaTableBody.innerHTML = auditorias.map(auditoria => {
        const descripcion = auditoria.valorNuevo || auditoria.valorAnterior || 'Sin descripción';
        const badgeClass = auditoria.operacion === 'INSERT' ? 'text-success' :
                           auditoria.operacion === 'DELETE' ? 'text-danger' : 'text-warning';
        return `
            <tr>
                <td><span class="fw-bold ${badgeClass}">${auditoria.operacion}</span></td>
                <td>${formatDate(auditoria.fecha)}</td>
                <td>${auditoria.nombreUsuario || 'Sin usuario'}</td>
                <td>${auditoria.entidad || 'Sin entidad'}</td>
                <td class="small" style="font-family: monospace; word-break: break-word;">${descripcion}</td>
            </tr>
        `;
    }).join('');
}

async function cargarAuditoria() {
    try {
        const auditorias = await apiFetch('/api/auditoria/public');
        if (auditoriasCountText) auditoriasCountText.innerText = `Mostrando: ${auditorias.length} registros`;
        renderAuditoriaRows(auditorias);
    } catch (error) {
        if (auditoriaTableBody) {
            auditoriaTableBody.innerHTML = `<tr><td colspan="5" class="text-center text-danger">${error.message}</td></tr>`;
        }
    }
}

async function cargarTodo() {
    await Promise.all([
        cargarDashboard(),
        cargarBodegas(),
        cargarProductos(),
        cargarMovimientos(),
        cargarAuditoria()
    ]);
}

async function cargarUsuariosEnSelect(selectElement, placeholder) {
    if (!selectElement) return;

    selectElement.disabled = true;
    selectElement.innerHTML = `<option value="">Cargando...</option>`;

    const usuarios = await apiFetch('/api/usuario/public');
    selectElement.innerHTML = `
        <option value="">${placeholder}</option>
        ${usuarios.map(usuario => `
            <option value="${usuario.id}">${usuario.nombre} (${usuario.username})</option>
        `).join('')}
    `;
    selectElement.disabled = false;
}

async function cargarBodegasEnSelect(selectElement, placeholder) {
    if (!selectElement) return;

    selectElement.disabled = true;
    selectElement.innerHTML = `<option value="">Cargando...</option>`;

    const bodegas = await apiFetch('/api/bodega/public');
    selectElement.innerHTML = `
        <option value="">${placeholder}</option>
        ${bodegas.map(bodega => `
            <option value="${bodega.id}">${bodega.nombre}</option>
        `).join('')}
    `;
    selectElement.disabled = false;
}

async function cargarProductosEnSelect(selectElement, placeholder) {
    if (!selectElement) return;

    selectElement.disabled = true;
    selectElement.innerHTML = `<option value="">Cargando...</option>`;

    const productos = await apiFetch('/api/producto/public');
    selectElement.innerHTML = `
        <option value="">${placeholder}</option>
        ${productos.map(producto => `
            <option value="${producto.id}">${producto.nombre}</option>
        `).join('')}
    `;
    selectElement.disabled = false;
}

async function cargarBodegasEnSelect(selectElement, placeholder) {
    if (!selectElement) return;

    const bodegas = await apiFetch('/api/bodega/public');
    selectElement.innerHTML = `
        <option value="">${placeholder}</option>
        ${bodegas.map(bodega => `
            <option value="${bodega.id}">${bodega.nombre}</option>
        `).join('')}
    `;
}

async function cargarProductosEnSelect(selectElement, placeholder) {
    if (!selectElement) return;

    const productos = await apiFetch('/api/producto/public');
    selectElement.innerHTML = `
        <option value="">${placeholder}</option>
        ${productos.map(producto => `
            <option value="${producto.id}">${producto.nombre}</option>
        `).join('')}
    `;
}

async function mostrarProductoForm() {
    if (!productoFormBox) return;
    ocultarTodosLosFormularios();
    productoFormBox.classList.remove('hidden');
    clearFormError(productoFormError);
    if (productoForm) productoForm.reset();

    try {
        await cargarBodegasEnSelect(productoBodegaId, 'Seleccione una bodega');
    } catch (error) {
        setFormError(productoFormError, error.message);
    }
}

async function mostrarBodegaForm() {
    if (!bodegaFormBox) return;
    ocultarTodosLosFormularios();
    bodegaFormBox.classList.remove('hidden');
    clearFormError(bodegaFormError);
    if (bodegaForm) bodegaForm.reset();

    try {
        await cargarUsuariosEnSelect(bodegaEncargadoId, 'Seleccione un encargado');
    } catch (error) {
        setFormError(bodegaFormError, error.message);
    }
}

function actualizarFormularioMovimientoSegunTipo() {
    if (!movTipo) return;

    const tipo = movTipo.value;

    if (tipo === 'ENTRADA') {
        if (movOrigenWrap) movOrigenWrap.classList.add('hidden');
        if (movDestinoWrap) movDestinoWrap.classList.remove('hidden');
        if (movBodegaOrigenId) movBodegaOrigenId.value = '';
        if (movBodegaOrigenId) movBodegaOrigenId.required = false;
        if (movBodegaDestinoId) movBodegaDestinoId.required = true;
    } else if (tipo === 'SALIDA') {
        if (movOrigenWrap) movOrigenWrap.classList.remove('hidden');
        if (movDestinoWrap) movDestinoWrap.classList.add('hidden');
        if (movBodegaDestinoId) movBodegaDestinoId.value = '';
        if (movBodegaDestinoId) movBodegaDestinoId.required = false;
        if (movBodegaOrigenId) movBodegaOrigenId.required = true;
    } else if (tipo === 'TRANSFERENCIA') {
        if (movOrigenWrap) movOrigenWrap.classList.remove('hidden');
        if (movDestinoWrap) movDestinoWrap.classList.remove('hidden');
        if (movBodegaOrigenId) movBodegaOrigenId.required = true;
        if (movBodegaDestinoId) movBodegaDestinoId.required = true;
    } else {
        if (movOrigenWrap) movOrigenWrap.classList.remove('hidden');
        if (movDestinoWrap) movDestinoWrap.classList.remove('hidden');
        if (movBodegaOrigenId) movBodegaOrigenId.required = false;
        if (movBodegaDestinoId) movBodegaDestinoId.required = false;
    }
}

async function mostrarMovimientoForm() {
    if (!movimientoFormBox) return;
    ocultarTodosLosFormularios();
    movimientoFormBox.classList.remove('hidden');
    clearFormError(movimientoFormError);
    if (movimientoForm) movimientoForm.reset();

    try {
        await Promise.all([
            cargarUsuariosEnSelect(movUsuarioId, 'Seleccione un usuario'),
            cargarBodegasEnSelect(movBodegaOrigenId, 'Seleccione una bodega'),
            cargarBodegasEnSelect(movBodegaDestinoId, 'Seleccione una bodega'),
            cargarProductosEnSelect(movProductoId, 'Seleccione un producto')
        ]);
        actualizarFormularioMovimientoSegunTipo();
    } catch (error) {
        setFormError(movimientoFormError, error.message);
    }
}

async function filtrarAuditoria() {
    clearFormError(auditoriaFilterError);

    const id = auditoriaIdFiltro?.value.trim();
    const usuario = auditoriaUsuarioFiltro?.value.trim();
    const operacion = auditoriaOperacionFiltro?.value.trim();

    // Contar cuantos filtros tienen valor
    const filtrosActivos = [id, usuario, operacion].filter(v => v !== '').length;

    if (filtrosActivos > 1) {
        setFormError(auditoriaFilterError, 'Usa solo un filtro a la vez: ID, usuario u operación.');
        return;
    }

    try {
        let auditorias = [];

        if (id) {
            const auditoria = await apiFetch(`/api/auditoria/${id}`);
            auditorias = [auditoria];
        } else if (usuario) {
            auditorias = await apiFetch(`/api/auditoria/usuario?nombre=${encodeURIComponent(usuario)}`);
        } else if (operacion) {
            auditorias = await apiFetch(`/api/auditoria/operacion?operacion=${encodeURIComponent(operacion)}`);
        } else {
            auditorias = await apiFetch('/api/auditoria/public');
        }

        if (auditoriasCountText) auditoriasCountText.innerText = `Mostrando: ${auditorias.length} registros`;
        renderAuditoriaRows(auditorias);
    } catch (error) {
        setFormError(auditoriaFilterError, error.message);
        if (auditoriaTableBody) {
            auditoriaTableBody.innerHTML = `<tr><td colspan="5" class="text-center text-danger">${error.message}</td></tr>`;
        }
    }
}

loginForm.addEventListener('submit', async function (e) {
    e.preventDefault();

    limpiarErrorLogin();

    const username = document.getElementById('userInput').value.trim();
    const password = document.getElementById('passwordInput').value.trim();

    try {
        const response = await apiFetch('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });

        setSession(response.token, username);
        showApp(username);
        await cargarTodo();
    } catch (error) {
        mostrarErrorLogin(error.message || 'Usuario o contraseña incorrectos');
    }
});

if (showRegister) {
    showRegister.addEventListener('click', function (e) {
        e.preventDefault();
        limpiarErrorLogin();
        loginCard.classList.add('hidden');
        registerCard.classList.remove('hidden');
    });
}

if (showLoginLink) {
    showLoginLink.addEventListener('click', function (e) {
        e.preventDefault();
        limpiarErrorLogin();
        registerCard.classList.add('hidden');
        loginCard.classList.remove('hidden');
    });
}

if (registerForm) {
    registerForm.addEventListener('submit', async function (e) {
        e.preventDefault();

        const nombre = document.getElementById('registerName').value.trim();
        const documento = document.getElementById('registerDocument').value.trim();
        const username = document.getElementById('registerUser').value.trim();
        const password = document.getElementById('registerPassword').value.trim();
        const rol = document.getElementById('registerRole').value;

        try {
            await apiFetch('/api/usuario', {
                method: 'POST',
                body: JSON.stringify({ nombre, documento, username, password, rol })
            });

            alert('Usuario registrado correctamente');
            registerForm.reset();
            registerCard.classList.add('hidden');
            loginCard.classList.remove('hidden');
        } catch (error) {
            alert(error.message);
        }
    });
}

if (productoForm) {
    productoForm.addEventListener('submit', async function (e) {
        e.preventDefault();

        clearFormError(productoFormError);

        const nombre = document.getElementById('productoNombre').value.trim();
        const categoria = document.getElementById('productoCategoria').value.trim();
        const precio = document.getElementById('productoPrecio').value.trim();
        const stock = document.getElementById('productoStock').value.trim();
        const bodegaId = document.getElementById('productoBodegaId').value;

        try {
            await apiFetch('/api/producto', {
                method: 'POST',
                body: JSON.stringify({
                    nombre,
                    categoria: categoria === '' ? null : categoria,
                    precio: Number(precio),
                    stock: Number(stock),
                    bodegaId: Number(bodegaId)
                })
            });

            alert('Producto registrado correctamente');
            ocultarTodosLosFormularios();
            await cargarTodo();
        } catch (error) {
            setFormError(productoFormError, error.message);
        }
    });
}

if (bodegaForm) {
    bodegaForm.addEventListener('submit', async function (e) {
        e.preventDefault();

        clearFormError(bodegaFormError);

        const nombre = document.getElementById('bodegaNombre').value.trim();
        const ubicacion = document.getElementById('bodegaUbicacion').value.trim();
        const capacidad = document.getElementById('bodegaCapacidad').value.trim();
        const encargadoId = document.getElementById('bodegaEncargadoId').value;

        try {
            await apiFetch('/api/bodega', {
                method: 'POST',
                body: JSON.stringify({
                    nombre,
                    ubicacion,
                    capacidad: Number(capacidad),
                    encargadoId: Number(encargadoId)
                })
            });

            alert('Bodega registrada correctamente');
            ocultarTodosLosFormularios();
            await cargarTodo();
        } catch (error) {
            setFormError(bodegaFormError, error.message);
        }
    });
}

if (movTipo) {
    movTipo.addEventListener('change', actualizarFormularioMovimientoSegunTipo);
}

if (movimientoForm) {
    movimientoForm.addEventListener('submit', async function (e) {
        e.preventDefault();

        clearFormError(movimientoFormError);

        const tipoMovimiento = movTipo.value;
        const usuarioId = movUsuarioId.value;
        const bodegaOrigenId = movBodegaOrigenId.value;
        const bodegaDestinoId = movBodegaDestinoId.value;
        const productoId = movProductoId.value;
        const cantidad = movCantidad.value;

        try {
            await apiFetch('/api/movimiento', {
                method: 'POST',
                body: JSON.stringify({
                    tipoMovimiento,
                    usuarioId: Number(usuarioId),
                    bodegaOrigenId: bodegaOrigenId ? Number(bodegaOrigenId) : null,
                    bodegaDestinoId: bodegaDestinoId ? Number(bodegaDestinoId) : null,
                    detalles: [
                        {
                            productoId: Number(productoId),
                            cantidad: Number(cantidad)
                        }
                    ]
                })
            });

            alert('Movimiento registrado correctamente');
            ocultarTodosLosFormularios();
            await cargarTodo();
        } catch (error) {
            setFormError(movimientoFormError, error.message);
        }
    });
}

if (auditoriaFilterForm) {
    auditoriaFilterForm.addEventListener('submit', async function (e) {
        e.preventDefault();
        await filtrarAuditoria();
    });
}

if (clearAuditoriaFiltersBtn) {
    clearAuditoriaFiltersBtn.addEventListener('click', async function () {
        if (auditoriaIdFiltro) auditoriaIdFiltro.value = '';
        if (auditoriaUsuarioFiltro) auditoriaUsuarioFiltro.value = '';
        if (auditoriaOperacionFiltro) auditoriaOperacionFiltro.value = '';
        clearFormError(auditoriaFilterError);
        await cargarAuditoria();
    });
}

if (cancelProductoBtn) {
    cancelProductoBtn.addEventListener('click', ocultarTodosLosFormularios);
}

if (cancelProductoBtnBottom) {
    cancelProductoBtnBottom.addEventListener('click', ocultarTodosLosFormularios);
}

if (cancelBodegaBtn) {
    cancelBodegaBtn.addEventListener('click', ocultarTodosLosFormularios);
}

if (cancelBodegaBtnBottom) {
    cancelBodegaBtnBottom.addEventListener('click', ocultarTodosLosFormularios);
}

if (cancelMovimientoBtn) {
    cancelMovimientoBtn.addEventListener('click', ocultarTodosLosFormularios);
}

if (cancelMovimientoBtnBottom) {
    cancelMovimientoBtnBottom.addEventListener('click', ocultarTodosLosFormularios);
}

logoutBtn.addEventListener('click', function (e) {
    e.preventDefault();
    clearSession();
    limpiarErrorLogin();
    ocultarTodosLosFormularios();
    showLogin();
    loginForm.reset();
});

menuItems.forEach(item => {
    item.addEventListener('click', function () {
        const sectionId = this.dataset.section;
        navigate(sectionId, this);
    });
});

function navigate(sectionId, element) {
    menuItems.forEach(item => item.classList.remove('active'));
    element.classList.add('active');

    pageTitle.innerText = titles[sectionId] || 'Panel';
    currentSection = sectionId;

    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.remove('active');
    });

    const target = document.getElementById('section-' + sectionId);
    if (target) {
        target.classList.add('active');
    }

    if (sectionId !== 'productos' && sectionId !== 'bodegas' && sectionId !== 'movimientos') {
        ocultarTodosLosFormularios();
    }

    // Mostrar btnNuevo solo donde tiene función
    const seccionesConNuevo = ['productos', 'bodegas', 'movimientos'];
    if (btnNuevo) {
        btnNuevo.style.display = seccionesConNuevo.includes(sectionId) ? '' : 'none';
    }
}

btnNuevo.addEventListener('click', async function () {
    if (currentSection === 'productos') {
        await mostrarProductoForm();
    } else if (currentSection === 'bodegas') {
        await mostrarBodegaForm();
    } else if (currentSection === 'movimientos') {
        await mostrarMovimientoForm();
    } else if (currentSection === 'auditoria') {
        if (auditoriaUsuarioFiltro) auditoriaUsuarioFiltro.focus();
    }
});

window.addEventListener('DOMContentLoaded', async () => {
    const token = getToken();
    if (token) {
        showApp(getCurrentUser());
        await cargarTodo();
    }
    // Ocultar btnNuevo en inicio al cargar
    if (btnNuevo) btnNuevo.style.display = 'none';
});
