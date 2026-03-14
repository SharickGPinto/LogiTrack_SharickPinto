// LOGIN
const loginForm = document.getElementById('loginForm');
const loginView = document.getElementById('login-view');
const appContainer = document.getElementById('app-container');
const displayUser = document.getElementById('displayUser');
const logoutBtn = document.getElementById('logoutBtn');

// NAVEGACIÓN
const menuItems = document.querySelectorAll('.menu-item');
const pageTitle = document.getElementById('page-title');
const searchInput = document.getElementById('tableSearch');
const btnNuevo = document.getElementById('btnNuevo');

let currentSection = 'inicio';

const titles = {
    inicio: 'Panel de Inicio',
    inventario: 'Control de Inventario',
    movimientos: 'Historial de Movimientos',
    ajustes: 'Configuración del Sistema'
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
        displayUser.innerText = user || 'Operador';
    }, 500);
});

// 2. CERRAR SESIÓN
logoutBtn.addEventListener('click', function (e) {
    e.preventDefault();
    location.reload();
});

// 3. CAMBIO DE SECCIONES
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

// 4. BOTÓN NUEVO
btnNuevo.addEventListener('click', function () {
    if (currentSection === 'inventario') {
        alert('Funcionalidad: Agregar nuevo producto');
    } else if (currentSection === 'movimientos') {
        alert('Funcionalidad: Registrar nuevo movimiento');
    } else if (currentSection === 'ajustes') {
        alert('Funcionalidad: Crear nueva configuración');
    } else {
        alert('Funcionalidad disponible según el módulo seleccionado');
    }
});

// 5. BUSCADOR
searchInput.addEventListener('keyup', doSearch);

function doSearch() {
    const filter = searchInput.value.toLowerCase();
    const inventorySection = document.getElementById('section-inventario');
    const inventoryTable = document.getElementById('inventoryTable');

    if (!inventoryTable) return;

    if (filter.length > 0 && currentSection !== 'inventario') {
        const inventarioBtn = document.querySelector('[data-section="inventario"]');
        navigate('inventario', inventarioBtn);
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

    if (filter.length === 0 && currentSection !== 'inventario') {
        inventorySection.classList.remove('active');
    }
}