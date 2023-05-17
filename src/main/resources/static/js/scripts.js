/*!
    * Start Bootstrap - SB Admin v7.0.7 (https://startbootstrap.com/template/sb-admin)
    * Copyright 2013-2023 Start Bootstrap
    * Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-sb-admin/blob/master/LICENSE)
    */
    // 
// Scripts
// 

window.addEventListener('DOMContentLoaded', event => {

    // Toggle the side navigation
    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {
        // Uncomment Below to persist sidebar toggle between refreshes
        // if (localStorage.getItem('sb|sidebar-toggle') === 'true') {
        //     document.body.classList.toggle('sb-sidenav-toggled');
        // }
        sidebarToggle.addEventListener('click', event => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
        });
    }

});

/* Bootstrap 5 JS included */
/* vanillajs-datepicker 1.1.4 JS included */

// date picker

const getDatePickerTitle = elem => {
    // From the label or the aria-label
    const label = elem.nextElementSibling;
    let titleText = '';
    if (label && label.tagName === 'LABEL') {
      titleText = label.textContent;
    } else {
      titleText = elem.getAttribute('aria-label') || '';
    }
    return titleText;
  }
  
  const elems = document.querySelectorAll('.datepicker_input');
  for (const elem of elems) {
    const datepicker = new Datepicker(elem, {
      'format': 'dd/mm/yyyy', // UK format
      title: getDatePickerTitle(elem)
    });
  } // date picker

// 긁어옴
$('#departureDate').datepicker({
    uiLibrary: 'bootstrap5'
    ,format: 'mm/dd'
});


const selector = document.getElementById('departureHome');
const list = [1, 2, 3, 5, 6, 8, 7];

// 모달창 열기
selector.addEventListener('click', function() {
    populateList();
    const myModal = new bootstrap.Modal(document.getElementById('myModal'));
    myModal.show();
});

// 리스트 동적으로 생성
function populateList() {
    const listElement = document.getElementById('list');
    listElement.innerHTML = '';

    for (let i = 0; i < list.length; i++) {
        const listItem = document.createElement('li');
        listItem.className = 'list-group-item';
        listItem.innerText = list[i];
        listItem.addEventListener('click', function() {
            updateSelectorValue(list[i]);
            $(".list-group-item").removeClass("active");
            $(this).addClass("active");
        });

        listElement.appendChild(listItem);
    }
}

// 선택된 리스트 요소로 selector의 value 업데이트
function updateSelectorValue(value) {
    selector.value = value;
}

// 확인 버튼 클릭 시 모달창 닫기 및 selector 업데이트
function updateSelector() {
    const myModal = bootstrap.Modal.getInstance(document.getElementById('myModal'));
    myModal.hide();
    // 선택된 리스트 요소의 값 표시
    console.log(selector.value);
}
// 긁어옴