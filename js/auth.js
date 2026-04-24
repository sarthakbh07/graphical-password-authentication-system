document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM fully loaded and parsed');

    const authForm = document.getElementById('authForm');
    const submitBtn = document.getElementById('submitBtn');
    const toggleModeBtn = document.getElementById('toggleModeBtn');

    console.log('authForm:', authForm);
    console.log('submitBtn:', submitBtn);
    console.log('toggleModeBtn:', toggleModeBtn);

    if (authForm) {
        authForm.addEventListener('submit', handleAuth);
        console.log('Submit event listener added to authForm');
    } else {
        console.error('authForm not found');
    }

    if (toggleModeBtn) {
        toggleModeBtn.addEventListener('click', toggleMode);
        console.log('Click event listener added to toggleModeBtn');
    } else {
        console.error('toggleModeBtn not found');
    }

    // Call populateImageGrid from imageSelection.js
    if (typeof populateImageGrid === 'function') {
        populateImageGrid();
    } else {
        console.error('populateImageGrid function not found');
    }
});

// No declaration of selectedImages here
let isLoginMode = true; // Keep track of the mode

function handleAuth(event) {
    console.log('handleAuth function called');
    event.preventDefault(); // Prevent the default form submission behavior

    const username = document.getElementById('username').value.trim(); // Trim whitespace
    const imagePattern = selectedImages.join(','); // Use selectedImages from imageSelection.js

    console.log('Username:', username);
    console.log('Image Pattern:', imagePattern);

    // Ensure the username and the image pattern are not empty
    if (username && selectedImages.length === 3) {
        const url = isLoginMode ? '/gauth/login' : '/gauth/signup';
        console.log('Sending request to:', url);

        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: username,
                imagePattern: imagePattern
            }),
        })
        .then(response => {
            console.log('Response received:', response);
            if (!response.ok) {
                return response.text().then(err => { throw new Error(err); });
            }
            return response.json();
        })
        .then(data => {
            console.log('Data received:', data);
            if (data.success) {
                alert(isLoginMode ? 'Login successful!' : 'Signup successful!');
                authForm.reset();
                
                if (typeof resetImageSelection === 'function') {
                    resetImageSelection(); // Clear image selection
                } else {
                    console.error('resetImageSelection function not found');
                }
                
                // Check for redirect URL in response
                if (data.redirect) {
                    window.location.href = data.redirect; // Redirect to the given URL
                }
            } else {
                alert(data.message || 'An error occurred');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred: ' + error.message);
        });
    } else {
        alert('Please fill in all fields and select 3 images.');
    }
}

function toggleMode() {
    console.log('toggleMode function called');
    isLoginMode = !isLoginMode;

    const submitBtn = document.getElementById('submitBtn');
    const toggleModeBtn = document.getElementById('toggleModeBtn');

    if (submitBtn) {
        submitBtn.textContent = isLoginMode ? 'Login' : 'Signup';
    }
    if (toggleModeBtn) {
        toggleModeBtn.textContent = isLoginMode ? 'Switch to Signup' : 'Switch to Login';
    }

    const authForm = document.getElementById('authForm');
    if (authForm) {
        authForm.reset(); // Reset the form fields
    }

    // Reset selected images when switching modes
    selectedImages = []; // Clear selectedImages
    if (typeof resetImageSelection === 'function') {
        resetImageSelection(); // Call the function to reset image selection
    }

    console.log('Mode switched to:', isLoginMode ? 'Login' : 'Signup');
}

