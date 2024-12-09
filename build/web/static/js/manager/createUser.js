
document.getElementById("loginLink").addEventListener("click", function (event) {
    event.preventDefault(); // Prevent the default action (which is link navigation)

    // You can add any logic here (like checking if the user is logged in, etc.)

    // Now, trigger navigation to the login page
    window.location.href = "login.html"; // Redirect
});






document.getElementById('signup-form').addEventListener('submit', function (event) {
    event.preventDefault();

    var phoneNumber = document.querySelector('input[name="phone"]').value;
    var email = document.querySelector('input[name="email"]').value;
    var role = document.querySelector('select[name="role"]').value;

    if (!phoneNumber || !email || !role) {
        document.getElementById('errorContainer').textContent = 'Please fill in all required fields';
        document.getElementById('errorContainer').style.display = 'block';
        return;
    }

    var formData = new FormData(this);
    fetch('/workforce_meal_coordination_system/restaurantManager/createUser', {
        method: 'POST',
        body: formData
    })
    .then(function (response) {
        if (!response.ok) {
            return response.json().then(function(errorData) {
                throw new Error(errorData.error || 'Network response was not ok');
            });
        }
        return response.json();
    })
    .then(function (data) {
        var errorContainer = document.getElementById('errorContainer');
        if (data.error) {
            errorContainer.textContent = data.error;
            errorContainer.style.display = 'block';
        } else {
            window.location.href = '/workforce_meal_coordination_system/manageUsers.html';
        }
    })
    .catch(function (error) {
        console.error('Error:', error);
        var errorContainer = document.getElementById('errorContainer');
        errorContainer.textContent = error.message || 'An error occurred during signup';
        errorContainer.style.display = 'block';
    });
});