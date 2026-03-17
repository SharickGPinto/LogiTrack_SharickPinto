const API_BASE = `http://${window.location.hostname}:8080`;

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
const registerRole = document.getElementById('registerRole');
const loginError = document.getElementById('loginError');

const menuItems = document.querySelectorAll('.menu-item');
const pageTitle = document.getElementById('page-title');
const btnNuevo = document.getElementById('btnNuevo');
const menuToggle = document.getElementById('menuToggle');
const sidebarOverlay = document.getElementById('sidebarOverlay');

const statProductos = document.getElementById('statProductos');
const statBodegas = document.getElementById('statBodegas');
const statMovimientos = document.getElementById('statMovimientos');
const statAuditorias = document.getElementById('statAuditorias');

const inicioStockBajo = document.getElementById('inicioStockBajo');
const inicioMovimientosRecientes = document.getElementById('inicioMovimientosRecientes');

const usuariosCountText = document.getElementById('usuariosCountText');
const bodegasCountText = document.getElementById('bodegasCountText');
const productosCountText = document.getElementById('productosCountText');
const auditoriasCountText = document.getElementById('auditoriasCountText');

const usuariosTableBody = document.getElementById('usuariosTableBody');
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

const usuariosFilterForm = document.getElementById('usuariosFilterForm');
const usuarioDocumentoFiltro = document.getElementById('usuarioDocumentoFiltro');
const usuariosFilterError = document.getElementById('usuariosFilterError');
const clearUsuariosFiltersBtn = document.getElementById('clearUsuariosFiltersBtn');

const bodegaFormTitulo = document.getElementById('bodegaFormTitulo');
const bodegaSubmitBtn = document.getElementById('bodegaSubmitBtn');
const productoFormTitulo = document.getElementById('productoFormTitulo');
const productoSubmitBtn = document.getElementById('productoSubmitBtn');

let bodegaEditId = null;
let productoEditId = null;
let currentSection = 'inicio';

const titles = {
    inicio: 'Inicio',
    usuarios: 'Gestión de Usuarios',
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

function getCurrentRole() {
    return localStorage.getItem('currentRole') || 'EMPLEADO';
}

function isAdmin() {
    return getCurrentRole() === 'ADMIN';
}

function setSession(token, username, rol) {
    localStorage.setItem('token', token);
    localStorage.setItem('currentUser', username);
    localStorage.setItem('currentRole', rol || 'EMPLEADO');
}

function clearSession() {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('currentRole');
}

function isMobileView() {
    return window.innerWidth <= 991;
}

function openSidebarMobile() {
    if (!appContainer || !isMobileView()) return;
    appContainer.classList.add('sidebar-open');
}

function closeSidebarMobile() {
    if (!appContainer) return;
    appContainer.classList.remove('sidebar-open');
}

function toggleSidebarMobile() {
    if (!appContainer || !isMobileView()) return;
    appContainer.classList.toggle('sidebar-open');
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

function configurarRolRegistro() {
    if (!registerRole) return;
    registerRole.innerHTML = '<option value="EMPLEADO">EMPLEADO</option>';
    registerRole.value = 'EMPLEADO';
    registerRole.setAttribute('disabled', 'disabled');
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
    if (displayUser) displayUser.innerText = username || 'Empleado';
    if (!loginView || !appContainer) return;

    loginView.style.opacity = '0';
    loginView.style.transition = '0.5s';

    setTimeout(() => {
        loginView.classList.add('hidden');
        appContainer.classList.remove('hidden');
    }, 300);
}

function showLogin() {
    if (loginView) loginView.classList.remove('hidden');
    if (appContainer) appContainer.classList.add('hidden');
    if (loginView) loginView.style.opacity = '1';
    if (displayUser) displayUser.innerText = 'Empleado';
    if (pageTitle) pageTitle.innerText = 'Inicio';
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

    bodegaEditId = null;
    productoEditId = null;

    if (bodegaFormTitulo) bodegaFormTitulo.innerText = 'Registrar Bodega';
    if (bodegaSubmitBtn) bodegaSubmitBtn.innerText = 'Guardar bodega';
    if (productoFormTitulo) productoFormTitulo.innerText = 'Registrar Producto';
    if (productoSubmitBtn) productoSubmitBtn.innerText = 'Guardar producto';
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

function renderUsuariosRows(usuarios) {
    if (!usuariosTableBody) return;

    if (!usuarios || usuarios.length === 0) {
        usuariosTableBody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No hay usuarios registrados</td></tr>';
        return;
    }

    usuariosTableBody.innerHTML = usuarios.map(usuario => `
        <tr>
            <td>${usuario.id}</td>
            <td><strong>${usuario.nombre}</strong></td>
            <td>${usuario.documento}</td>
            <td>${usuario.username}</td>
            <td><span class="status-badge status-ok">${usuario.rol}</span></td>
            <td>
                ${isAdmin() && usuario.username !== getCurrentUser() ? `
                    <button class="btn btn-sm btn-outline-danger" onclick="eliminarUsuario('${usuario.documento}', '${usuario.nombre.replace(/'/g, "\\'")}')">
                        Eliminar
                    </button>
                ` : '<span class="text-muted small">Sin acciones</span>'}
            </td>
        </tr>
    `).join('');
}

async function cargarUsuarios() {
    try {
        const usuarios = await apiFetch('/api/usuario/public');
        if (usuariosCountText) usuariosCountText.innerText = `Mostrando: ${usuarios.length} registros`;
        renderUsuariosRows(usuarios);
    } catch (error) {
        if (usuariosTableBody) {
            usuariosTableBody.innerHTML = `<tr><td colspan="6" class="text-center text-danger">${error.message}</td></tr>`;
        }
    }
}

async function filtrarUsuarios() {
    clearFormError(usuariosFilterError);

    const documento = usuarioDocumentoFiltro?.value.trim();

    try {
        let usuarios = [];

        if (documento) {
            const usuario = await apiFetch(`/api/usuario/${encodeURIComponent(documento)}`);
            usuarios = [usuario];
        } else {
            usuarios = await apiFetch('/api/usuario/public');
        }

        if (usuariosCountText) usuariosCountText.innerText = `Mostrando: ${usuarios.length} registros`;
        renderUsuariosRows(usuarios);
    } catch (error) {
        setFormError(usuariosFilterError, error.message);
        if (usuariosTableBody) {
            usuariosTableBody.innerHTML = `<tr><td colspan="6" class="text-center text-danger">${error.message}</td></tr>`;
        }
    }
}

async function cargarBodegas() {
    try {
        const bodegas = await apiFetch('/api/bodega/public');
        if (bodegasCountText) bodegasCountText.innerText = `Mostrando: ${bodegas.length} registros`;

        if (bodegasTableBody) {
            if (bodegas.length === 0) {
                bodegasTableBody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No hay bodegas registradas</td></tr>';
                return;
            }

            bodegasTableBody.innerHTML = bodegas.map(bodega => `
                <tr>
                    <td>${bodega.id}</td>
                    <td><strong>${bodega.nombre}</strong></td>
                    <td>${bodega.ubicacion}</td>
                    <td>${bodega.capacidad}</td>
                    <td>${bodega.nombreEncargado || 'Sin encargado'}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="editarBodega(${bodega.id})">
                            <i class="fas fa-pen"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="eliminarBodega(${bodega.id}, '${bodega.nombre.replace(/'/g, "\\'")}')">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (error) {
        if (bodegasTableBody) {
            bodegasTableBody.innerHTML = `<tr><td colspan="6" class="text-center text-danger">${error.message}</td></tr>`;
        }
    }
}

async function cargarProductos() {
    try {
        const productos = await apiFetch('/api/producto/public');
        if (productosCountText) productosCountText.innerText = `Mostrando: ${productos.length} registros`;

        if (productosTableBody) {
            if (productos.length === 0) {
                productosTableBody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No hay productos registrados</td></tr>';
                return;
            }

            productosTableBody.innerHTML = productos.map(producto => {
                const status = getProductStatus(producto.stock);
                const nombreSeguro = (producto.nombre || '').replace(/'/g, "\\'");
                return `
                    <tr>
                        <td>${producto.id}</td>
                        <td><strong>${producto.nombre}</strong></td>
                        <td>${producto.categoria || 'Sin categoría'}</td>
                        <td>${producto.stock}</td>
                        <td>${formatPrice(producto.precio)}</td>
                        <td><span class="status-badge ${status.className}">${status.text}</span></td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary me-1" onclick="editarProducto(${producto.id})">
                                <i class="fas fa-pen"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger" onclick="eliminarProducto(${producto.id}, '${nombreSeguro}')">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    </tr>
                `;
            }).join('');
        }
    } catch (error) {
        if (productosTableBody) {
            productosTableBody.innerHTML = `<tr><td colspan="7" class="text-center text-danger">${error.message}</td></tr>`;
        }
    }
}

async function cargarMovimientos() {
    try {
        const movimientos = await apiFetch('/api/movimiento/public');

        if (movimientosList) {
            if (movimientos.length === 0) {
                movimientosList.innerHTML = '<div class="text-center text-muted py-3">No hay movimientos registrados</div>';
                return;
            }

            movimientosList.innerHTML = movimientos.map(movimiento => `
                <div class="p-3 mb-2 bg-light border-start ${getMovimientoClass(movimiento.tipoMovimiento)} border-4 d-flex justify-content-between align-items-start flex-column flex-md-row gap-2">
                    <div>
                        <strong>${movimiento.tipoMovimiento}:</strong> ${buildMovimientoText(movimiento)}
                        <div class="text-muted small mt-1">${formatDate(movimiento.fecha)} | Usuario: ${movimiento.nombreUsuario || 'Sin usuario'}</div>
                    </div>
                    <button class="btn btn-sm btn-outline-danger ms-md-3 flex-shrink-0" onclick="eliminarMovimiento(${movimiento.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            `).join('');
        }
    } catch (error) {
        if (movimientosList) {
            movimientosList.innerHTML = `<div class="text-center text-danger py-3">${error.message}</div>`;
        }
    }
}

function renderAuditoriaRows(auditorias) {
    if (!auditoriaTableBody) return;

    if (!auditorias || auditorias.length === 0) {
        auditoriaTableBody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No hay auditorías registradas</td></tr>';
        return;
    }

    auditoriaTableBody.innerHTML = auditorias.map(auditoria => {
        const descripcion = auditoria.valorNuevo || auditoria.valorAnterior || 'Sin descripción';
        const badgeClass = auditoria.operacion === 'INSERT'
            ? 'text-success'
            : auditoria.operacion === 'DELETE'
                ? 'text-danger'
                : 'text-warning';

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
        cargarUsuarios(),
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

async function editarBodega(id) {
    try {
        const bodega = await apiFetch(`/api/bodega/${id}`);

        ocultarTodosLosFormularios();
        if (bodegaFormBox) bodegaFormBox.classList.remove('hidden');
        clearFormError(bodegaFormError);

        await cargarUsuariosEnSelect(bodegaEncargadoId, 'Seleccione un encargado');

        document.getElementById('bodegaNombre').value = bodega.nombre;
        document.getElementById('bodegaUbicacion').value = bodega.ubicacion;
        document.getElementById('bodegaCapacidad').value = bodega.capacidad;

        const opciones = bodegaEncargadoId.options;
        for (let i = 0; i < opciones.length; i++) {
            if (opciones[i].text.startsWith(bodega.nombreEncargado)) {
                bodegaEncargadoId.selectedIndex = i;
                break;
            }
        }

        bodegaEditId = id;
        if (bodegaFormTitulo) bodegaFormTitulo.innerText = 'Editar Bodega';
        if (bodegaSubmitBtn) bodegaSubmitBtn.innerText = 'Actualizar bodega';
    } catch (error) {
        alert(error.message);
    }
}

async function eliminarBodega(id, nombre) {
    if (!confirm(`¿Seguro que deseas eliminar la bodega "${nombre}"?`)) return;

    try {
        await apiFetch(`/api/bodega/${id}`, { method: 'DELETE' });
        await cargarTodo();
    } catch (error) {
        alert(error.message);
    }
}

async function editarProducto(id) {
    try {
        const producto = await apiFetch(`/api/producto/${id}`);

        ocultarTodosLosFormularios();
        if (productoFormBox) productoFormBox.classList.remove('hidden');
        clearFormError(productoFormError);

        await cargarBodegasEnSelect(productoBodegaId, 'Seleccione una bodega');

        document.getElementById('productoNombre').value = producto.nombre;
        document.getElementById('productoCategoria').value = producto.categoria || '';
        document.getElementById('productoPrecio').value = producto.precio;
        document.getElementById('productoStock').value = producto.stock;

        const opciones = productoBodegaId.options;
        for (let i = 0; i < opciones.length; i++) {
            if (opciones[i].text === producto.bodega?.nombre) {
                productoBodegaId.selectedIndex = i;
                break;
            }
        }

        productoEditId = id;
        if (productoFormTitulo) productoFormTitulo.innerText = 'Editar Producto';
        if (productoSubmitBtn) productoSubmitBtn.innerText = 'Actualizar producto';
    } catch (error) {
        alert(error.message);
    }
}

async function eliminarProducto(id, nombre) {
    if (!confirm(`¿Seguro que deseas eliminar el producto "${nombre}"?`)) return;

    try {
        await apiFetch(`/api/producto/${id}`, { method: 'DELETE' });
        alert('Producto eliminado correctamente');
        await cargarTodo();
    } catch (error) {
        if (error.message && error.message.includes('stock fue puesto en 0')) {
            alert('Este producto tiene movimientos registrados.\nSu stock fue puesto en 0 para conservar el historial.');
            await cargarTodo();
        } else {
            alert(error.message);
        }
    }
}

async function eliminarMovimiento(id) {
    if (!confirm('¿Seguro que deseas eliminar este movimiento? Se revertirá el stock.')) return;

    try {
        await apiFetch(`/api/movimiento/${id}`, { method: 'DELETE' });
        await cargarTodo();
    } catch (error) {
        alert(error.message);
    }
}

async function eliminarUsuario(documento, nombre) {
    if (!confirm(`¿Seguro que deseas eliminar al usuario "${nombre}"?`)) return;

    try {
        await apiFetch(`/api/usuario/${encodeURIComponent(documento)}`, {
            method: 'DELETE'
        });

        alert('Usuario eliminado correctamente');
        await cargarUsuarios();
    } catch (error) {
        alert(error.message);
    }
}

if (loginForm) {
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

            setSession(response.token, username, response.rol);
            showApp(username);
            await cargarTodo();
        } catch (error) {
            mostrarErrorLogin(error.message || 'Usuario o contraseña incorrectos');
        }
    });
}

if (showRegister) {
    showRegister.addEventListener('click', function (e) {
        e.preventDefault();
        limpiarErrorLogin();
        configurarRolRegistro();
        if (loginCard) loginCard.classList.add('hidden');
        if (registerCard) registerCard.classList.remove('hidden');
    });
}

if (showLoginLink) {
    showLoginLink.addEventListener('click', function (e) {
        e.preventDefault();
        limpiarErrorLogin();
        if (registerCard) registerCard.classList.add('hidden');
        if (loginCard) loginCard.classList.remove('hidden');
    });
}

if (registerForm) {
    registerForm.addEventListener('submit', async function (e) {
        e.preventDefault();

        const nombre = document.getElementById('registerName').value.trim();
        const documento = document.getElementById('registerDocument').value.trim();
        const username = document.getElementById('registerUser').value.trim();
        const password = document.getElementById('registerPassword').value.trim();
        const rol = 'EMPLEADO';

        try {
            await apiFetch('/api/usuario', {
                method: 'POST',
                body: JSON.stringify({ nombre, documento, username, password, rol })
            });

            alert('Usuario registrado correctamente');
            registerForm.reset();
            if (registerCard) registerCard.classList.add('hidden');
            if (loginCard) loginCard.classList.remove('hidden');
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

        const body = JSON.stringify({
            nombre,
            categoria: categoria === '' ? null : categoria,
            precio: Number(precio),
            stock: Number(stock),
            bodegaId: Number(bodegaId)
        });

        try {
            if (productoEditId) {
                await apiFetch(`/api/producto/${productoEditId}`, { method: 'PUT', body });
                alert('Producto actualizado correctamente');
            } else {
                await apiFetch('/api/producto', { method: 'POST', body });
                alert('Producto registrado correctamente');
            }

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

        const body = JSON.stringify({
            nombre,
            ubicacion,
            capacidad: Number(capacidad),
            encargadoId: Number(encargadoId)
        });

        try {
            if (bodegaEditId) {
                await apiFetch(`/api/bodega/${bodegaEditId}`, { method: 'PUT', body });
                alert('Bodega actualizada correctamente');
            } else {
                await apiFetch('/api/bodega', { method: 'POST', body });
                alert('Bodega registrada correctamente');
            }

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

if (usuariosFilterForm) {
    usuariosFilterForm.addEventListener('submit', async function (e) {
        e.preventDefault();
        await filtrarUsuarios();
    });
}

if (clearUsuariosFiltersBtn) {
    clearUsuariosFiltersBtn.addEventListener('click', async function () {
        if (usuarioDocumentoFiltro) usuarioDocumentoFiltro.value = '';
        clearFormError(usuariosFilterError);
        await cargarUsuarios();
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

if (menuToggle) {
    menuToggle.addEventListener('click', function () {
        toggleSidebarMobile();
    });
}

if (sidebarOverlay) {
    sidebarOverlay.addEventListener('click', function () {
        closeSidebarMobile();
    });
}

if (logoutBtn) {
    logoutBtn.addEventListener('click', function (e) {
        e.preventDefault();
        clearSession();
        limpiarErrorLogin();
        ocultarTodosLosFormularios();
        closeSidebarMobile();
        showLogin();
        if (loginForm) loginForm.reset();
    });
}

menuItems.forEach(item => {
    item.addEventListener('click', function () {
        const sectionId = this.dataset.section;
        navigate(sectionId, this);
    });
});

function navigate(sectionId, element) {
    menuItems.forEach(item => item.classList.remove('active'));
    element.classList.add('active');

    if (pageTitle) pageTitle.innerText = titles[sectionId] || 'Panel';
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

    const seccionesConNuevo = ['productos', 'bodegas', 'movimientos'];
    if (btnNuevo) {
        btnNuevo.style.display = seccionesConNuevo.includes(sectionId) ? '' : 'none';
    }

    closeSidebarMobile();
}

if (btnNuevo) {
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
}

window.addEventListener('resize', function () {
    if (!isMobileView()) {
        closeSidebarMobile();
    }
});

window.addEventListener('DOMContentLoaded', async () => {
    configurarRolRegistro();
    closeSidebarMobile();

    const token = getToken();
    if (token) {
        showApp(getCurrentUser());
        await cargarTodo();
    }

    if (btnNuevo) btnNuevo.style.display = 'none';
});