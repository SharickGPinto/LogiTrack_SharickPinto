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
const searchInput = document.getElementById('tableSearch');
const btnNuevo = document.getElementById('btnNuevo');

const statProductos = document.getElementById('statProductos');
const statBodegas = document.getElementById('statBodegas');
const statMovimientos = document.getElementById('statMovimientos');
const statAuditorias = document.getElementById('statAuditorias');

const bodegasCountText = document.getElementById('bodegasCountText');
const productosCountText = document.getElementById('productosCountText');
const auditoriasCountText = document.getElementById('auditoriasCountText');

const bodegasTableBody = document.getElementById('bodegasTableBody');
const productosTableBody = document.getElementById('productosTableBody');
const auditoriaTableBody = document.getElementById('auditoriaTableBody');
const movimientosList = document.getElementById('movimientosList');

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
    loginError.textContent = mensaje;
    loginError.classList.remove('d-none');
}

function limpiarErrorLogin() {
    loginError.textContent = '';
    loginError.classList.add('d-none');
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
    if (data.errors) {
        return Object.values(data.errors).join('\n');
    }
    return null;
}

function formatPrice(value) {
    const number = Number(value || 0);
    return number.toLocaleString('es-CO', {
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
    if (stock <= 0) {
        return { text: 'Agotado', className: 'status-out' };
    }
    if (stock < 10) {
        return { text: 'Stock bajo', className: 'status-low' };
    }
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
}

async function cargarDashboard() {
    try {
        const [bodegas, productos, movimientos, auditorias] = await Promise.all([
            apiFetch('/api/bodega/public'),
            apiFetch('/api/producto/public'),
            apiFetch('/api/movimiento/public'),
            apiFetch('/api/auditoria/public')
        ]);

        statBodegas.innerText = bodegas.length;
        statProductos.innerText = productos.length;
        statMovimientos.innerText = movimientos.length;
        statAuditorias.innerText = auditorias.length;
    } catch (error) {
        console.error(error);
    }
}

async function cargarBodegas() {
    try {
        const bodegas = await apiFetch('/api/bodega/public');
        bodegasCountText.innerText = `Mostrando: ${bodegas.length} registros`;

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
    } catch (error) {
        bodegasTableBody.innerHTML = `<tr><td colspan="5" class="text-center text-danger">${error.message}</td></tr>`;
    }
}

async function cargarProductos() {
    try {
        const productos = await apiFetch('/api/producto/public');
        productosCountText.innerText = `Mostrando: ${productos.length} registros`;

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
    } catch (error) {
        productosTableBody.innerHTML = `<tr><td colspan="6" class="text-center text-danger">${error.message}</td></tr>`;
    }
}

async function cargarMovimientos() {
    try {
        const movimientos = await apiFetch('/api/movimiento/public');

        movimientosList.innerHTML = movimientos.map(movimiento => `
            <div class="p-3 mb-2 bg-light border-start ${getMovimientoClass(movimiento.tipoMovimiento)} border-4">
                <strong>${movimiento.tipoMovimiento}:</strong> ${buildMovimientoText(movimiento)}
                <div class="text-muted small mt-1">${formatDate(movimiento.fecha)} | Usuario: ${movimiento.nombreUsuario || 'Sin usuario'}</div>
            </div>
        `).join('');

        if (movimientos.length === 0) {
            movimientosList.innerHTML = '<div class="text-center text-muted py-3">No hay movimientos registrados</div>';
        }
    } catch (error) {
        movimientosList.innerHTML = `<div class="text-center text-danger py-3">${error.message}</div>`;
    }
}

async function cargarAuditoria() {
    try {
        const auditorias = await apiFetch('/api/auditoria/public');
        auditoriasCountText.innerText = `Mostrando: ${auditorias.length} registros`;

        auditoriaTableBody.innerHTML = auditorias.map(auditoria => `
            <tr>
                <td>${auditoria.operacion}</td>
                <td>${formatDate(auditoria.fecha)}</td>
                <td>${auditoria.nombreUsuario || 'Sin usuario'}</td>
                <td>${auditoria.entidad || 'Sin entidad'}</td>
                <td>${auditoria.valorNuevo || auditoria.valorAnterior || 'Sin descripción'}</td>
            </tr>
        `).join('');

        if (auditorias.length === 0) {
            auditoriaTableBody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No hay auditorías registradas</td></tr>';
        }
    } catch (error) {
        auditoriaTableBody.innerHTML = `<tr><td colspan="5" class="text-center text-danger">${error.message}</td></tr>`;
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

logoutBtn.addEventListener('click', function (e) {
    e.preventDefault();
    clearSession();
    limpiarErrorLogin();
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
}

btnNuevo.addEventListener('click', function () {
    if (currentSection === 'bodegas') {
        alert('Esta vista ya consume el backend para listar bodegas. El formulario de creación todavía no está en esta maqueta.');
    } else if (currentSection === 'productos') {
        alert('Esta vista ya consume el backend para listar productos. El formulario de creación todavía no está en esta maqueta.');
    } else if (currentSection === 'movimientos') {
        alert('Esta vista ya consume el backend para listar movimientos. El formulario de creación todavía no está en esta maqueta.');
    } else if (currentSection === 'auditoria') {
        alert('Esta vista ya consume el backend real para consultar auditoría.');
    } else {
        alert('Selecciona un módulo para ver la información conectada al backend.');
    }
});

searchInput.addEventListener('keyup', doSearch);

function doSearch() {
    const filter = searchInput.value.toLowerCase();
    const inventoryTable = document.getElementById('inventoryTable');

    if (!inventoryTable) return;

    if (filter.length > 0 && currentSection !== 'productos') {
        const productosBtn = document.querySelector('[data-section="productos"]');
        navigate('productos', productosBtn);
    }

    const rows = inventoryTable.querySelectorAll('tbody tr');

    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(filter) ? '' : 'none';
    });
}

window.addEventListener('DOMContentLoaded', async () => {
    const token = getToken();
    if (token) {
        showApp(getCurrentUser());
        await cargarTodo();
    }
});
