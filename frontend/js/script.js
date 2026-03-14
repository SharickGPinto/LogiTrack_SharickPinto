// LOGIN
const loginForm = document.getElementById('loginForm');
const loginView = document.getElementById('login-view');
const appContainer = document.getElementById('app-container');
const displayUser = document.getElementById('displayUser');
const logoutBtn = document.getElementById('logoutBtn');

// LOGIN / REGISTRO
const loginCard = document.getElementById('login-card');
const registerCard = document.getElementById('register-card');
const showRegister = document.getElementById('showRegister');
const showLogin = document.getElementById('showLogin');
const registerForm = document.getElementById('registerForm');

// NAVEGACIÓN
const menuItems = document.querySelectorAll('.menu-item');
const pageTitle = document.getElementById('page-title');
const searchInput = document.getElementById('tableSearch');
const btnNuevo = document.getElementById('btnNuevo');

let currentSection = 'inicio';

const titles = {
    inicio: 'Inicio',
    bodegas: 'Gestión de Bodegas',
    productos: 'Gestión de Productos',
    movimientos: 'Movimientos de Inventario',
    auditoria: 'Auditoría de Cambios'
};

// 1. LOGIN
loginForm.addEventListener('submit', function (e) {
    e.preventDefault();

    const user = document.getElementById('userInput').value.trim();

    loginView.style.opacity = '0';
    loginView.style.transition = '0.5s';

    setTimeout(() => {
        loginView.classList.add('hidden');
        appContainer.classList.remove('hidden');
        displayUser.innerText = user || 'Empleado';
    }, 500);
});

// 2. MOSTRAR FORMULARIO DE REGISTRO
if (showRegister) {
    showRegister.addEventListener('click', function (e) {
        e.preventDefault();
        loginCard.classList.add('hidden');
        registerCard.classList.remove('hidden');
    });
}

// 3. VOLVER AL LOGIN
if (showLogin) {
    showLogin.addEventListener('click', function (e) {
        e.preventDefault();
        registerCard.classList.add('hidden');
        loginCard.classList.remove('hidden');
    });
}

// 4. REGISTRO SIMULADO
if (registerForm) {
    registerForm.addEventListener('submit', function (e) {
        e.preventDefault();

        const nombre = document.getElementById('registerName').value.trim();
        const documento = document.getElementById('registerDocument').value.trim();
        const usuario = document.getElementById('registerUser').value.trim();
        const password = document.getElementById('registerPassword').value.trim();
        const rol = document.getElementById('registerRole').value;

        if (!nombre || !documento || !usuario || !password || !rol) {
            alert('Por favor complete todos los campos');
            return;
        }

        alert('Usuario registrado correctamente (simulado)');

        registerForm.reset();
        registerCard.classList.add('hidden');
        loginCard.classList.remove('hidden');
    });
}

// 5. CERRAR SESIÓN
logoutBtn.addEventListener('click', function (e) {
    e.preventDefault();
    location.reload();
});

// 6. CAMBIO DE SECCIONES
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

// 7. BOTÓN NUEVO
btnNuevo.addEventListener('click', function () {
    if (currentSection === 'bodegas') {
        alert('Funcionalidad: Registrar nueva bodega');
    } else if (currentSection === 'productos') {
        alert('Funcionalidad: Registrar nuevo producto');
    } else if (currentSection === 'movimientos') {
        alert('Funcionalidad: Registrar nuevo movimiento');
    } else if (currentSection === 'auditoria') {
        alert('Funcionalidad: Consultar registros de auditoría');
    } else {
        alert('Funcionalidad disponible según el módulo seleccionado');
    }
});

// 8. BUSCADOR
searchInput.addEventListener('keyup', doSearch);

function doSearch() {
    const filter = searchInput.value.toLowerCase();
    const productosSection = document.getElementById('section-productos');
    const inventoryTable = document.getElementById('inventoryTable');

    if (!inventoryTable) return;

    if (filter.length > 0 && currentSection !== 'productos') {
        const productosBtn = document.querySelector('[data-section="productos"]');
        navigate('productos', productosBtn);
    }

    const rows = inventoryTable.querySelectorAll('tbody tr');

    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        if (text.includes(filter)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });

    if (filter.length === 0 && currentSection !== 'productos') {
        productosSection.classList.remove('active');
    }
}