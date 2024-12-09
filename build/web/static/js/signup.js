
document.getElementById("loginLink").addEventListener("click", function(event) {
event.preventDefault(); // Prevent the default action (which is link navigation)

        // You can add any logic here (like checking if the user is logged in, etc.)

        // Now, trigger navigation to the login page
        window.location.href = "login.html"; // Redirect
        });
 
 
 
 
 
 
document.getElementById('signup-form').addEventListener('submit', function (event) {
    event.preventDefault(); // Prevent default form submission

    // Validate form inputs before submission
    var phoneNumber = document.querySelector('input[name="phone"]').value;
    var email = document.querySelector('input[name="email"]').value;
    var role = document.querySelector('select[name="role"]').value;

    // Basic validation
    if (!phoneNumber || !email || !role) {
        document.getElementById('error').textContent = 'Please fill in all required fields';
        document.getElementById('error').style.display = 'block';
        return;
    }

    var formData = new FormData(this);

    // Send the data to the backend via AJAX
    fetch('/workforce_meal_coordination_system/admin/createUser', {
        method: 'POST',
        body: formData
    })
    .then(function (response) {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(function (data) {
        var errorMessage = document.getElementById('error');
        if (data.error) {
            // If there's an error, display it
            errorMessage.textContent = data.error;
            errorMessage.style.display = 'block';
        } else if (data.message) {
            // If it's a success message, redirect the user to manage users page
            window.location.href = '/workforce_meal_coordination_system/manageUsers.html';
        }
    })
    .catch(function (error) {
        console.error('Error:', error);
        document.getElementById('error').textContent = 'An error occurred during signup';
        document.getElementById('error').style.display = 'block';
    });
});