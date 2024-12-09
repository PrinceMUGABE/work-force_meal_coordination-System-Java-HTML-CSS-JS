document.addEventListener('DOMContentLoaded', function () {
    var loginForm = document.getElementById('login-form');
    var errorContainer = document.getElementById('errorContainer');

    if (loginForm && errorContainer) {
        loginForm.addEventListener('submit', function (event) {
            event.preventDefault(); // Prevent default form submission

            var phoneOrEmail = document.getElementById('phoneOrEmail').value.trim();
            var password = document.getElementById('password').value;

            if (!phoneOrEmail || !password) {
                errorContainer.textContent = 'Please enter both phone/email and password';
                errorContainer.style.display = 'block';
                return;
            }

            var formData = {
                phoneOrEmail: phoneOrEmail,
                password: password
            };

            fetch('login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            })
            .then(function (response) {
                if (!response.ok) {
                    throw new Error('Login failed.');
                }
                return response.json();
            })
            .catch(function (error) {
                console.error('Error:', error);
                errorContainer.textContent = 'Invalid credentials or server error.';
                errorContainer.style.display = 'block';
            });
        });
    } else {
        console.error('Login form or error container not found!');
    }
});




document.getElementById("signupLink").addEventListener("click", function(event) {
event.preventDefault(); // Prevent the default action (which is link navigation)

        // You can add any logic here (like checking if the user is logged in, etc.)

        // Now, trigger navigation to the login page
        window.location.href = "login.html"; // Redirect to the login page
});



