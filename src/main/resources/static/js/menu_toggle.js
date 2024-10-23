document.addEventListener('DOMContentLoaded', function () {
  const menuToggle = document.querySelector('.menu-toggle');
  const menu = document.querySelector('.menu');
  const buttons = document.querySelector('.buttons');

  menuToggle.addEventListener('click', function () {
      menu.classList.toggle('active');
      buttons.classList.toggle('active');
  });
});

