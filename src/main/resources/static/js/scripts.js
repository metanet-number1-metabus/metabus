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


// 모바일 접근/ 웹 접근
function mobileReact() {

    const isMobile = window.matchMedia("(max-width: 576px)").matches;

    if (isMobile) {
        // 모바일접근시
        const elements = document.getElementsByClassName('mobile-font-header');

        // carouselExampleDark 요소의 m-5 클래스 제거
        const carouselExampleDark = document.getElementById('carouselExampleDark');

        // container-fluid rounded shadow-lg 클래스인 요소의 width를 260px로 설정
        const containerElement = document.querySelector('.container-fluid.rounded.shadow-lg');

        if (carouselExampleDark){
            carouselExampleDark.classList.remove('m-5');
        }

        if (containerElement){
            containerElement.style.width = '260px';
        }

        // user님 환영합니다. <-- 사이즈 깨짐으로 조절
        for (let i = 0; i < elements.length; i++) {
            elements[i].style.fontSize = '11px';
        }

    } else {
        //웹접근시
    }
}

  // 브라우저가 로딩되고 실행
document.addEventListener('DOMContentLoaded', mobileReact);

