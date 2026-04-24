// Configuration and state management
const CONFIG = {
    imageDirectory: 'images/',
    maxGridSize: 9,
    maxSelections: 3,
    totalImages: [
        '1.jpg', '2.jpg', '3.jpg', '4.jpg', '5.jpg',
        '6.jpg', '7.jpg', '8.jpg', '9.jpg', '10.jpg',
        '11.jpg', '12.jpg', '13.jpg', '14.jpg', '15.jpg',
        '16.jpg', '17.jpg', '18.jpg', '19.jpg', '20.jpg',
        '21.jpg', '22.jpg', '23.jpg', '24.jpg', '25.jpg',
        '26.jpg', '27.jpg', '28.jpg', '29.jpg', '30.jpg'
    ]
};

// Track selected images
let selectedImages = [];

// Utility function to shuffle array
function shuffleArray(array) {
    const shuffled = [...array];
    for (let i = shuffled.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
    }
    return shuffled;
}

// Populate image grid with random images
function populateImageGrid() {
    const imageGrid = document.getElementById('imageGrid');
    imageGrid.innerHTML = ''; // Clear existing images

    const randomImages = shuffleArray([...CONFIG.totalImages]).slice(0, CONFIG.maxGridSize);
    
    randomImages.forEach((imgName) => {
        const imgElement = document.createElement('img');
        imgElement.src = CONFIG.imageDirectory + imgName;
        imgElement.classList.add('image-item');
        imgElement.dataset.index = imgName; // Store original name as data attribute
        imgElement.addEventListener('click', toggleImageSelection); // Add click handler
        imageGrid.appendChild(imgElement); // Add image to grid
    });

    console.log("Images have been populated");
}

// Function to reset image selection
function resetImageSelection() {
    selectedImages = []; // Clear the selected images array
    const selectedElements = document.querySelectorAll('.image-item.selected');
    selectedElements.forEach(element => {
        element.classList.remove('selected'); // Remove selection styling
    });
    console.log('Image selection has been reset');
}
// Handle image selection
function toggleImageSelection(event) {
    const imgElement = event.target;
    const imgIndex = imgElement.dataset.index;

    if (selectedImages.includes(imgIndex)) {
        // Deselect image
        selectedImages = selectedImages.filter(index => index !== imgIndex);
        imgElement.classList.remove('selected');
    } else {
        // Select image if under max limit
        if (selectedImages.length < CONFIG.maxSelections) {
            selectedImages.push(imgIndex);
            imgElement.classList.add('selected');
        } else {
            alert(`You can only select up to ${CONFIG.maxSelections} images.`);
        }
    }
}

// Fetch images for username
async function fetchImagesByUsername(username) {
    try {
        const response = await fetch('/gauth/getImages', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username: username })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Fetched images for username:', data);
        return data.imagePattern ? data.imagePattern.split(',') : [];
    } catch (error) {
        console.error('Error fetching images:', error);
        return []; // Return an empty array in case of an error
    }
}
async function updateGridForUsername(username) {
    const userImages = await fetchImagesByUsername(username);
    const imageGrid = document.getElementById('imageGrid');
    imageGrid.innerHTML = ''; // Clear existing images

    let imagesToDisplay = []; // Array to hold images to display

    if (userImages.length > 0) {
        // Add user-specific images to the display array
        imagesToDisplay.push(...userImages);
        console.log("User images fetched:", userImages);
    }

    // Calculate how many more random images are needed
    const remainingSlots = CONFIG.maxGridSize - imagesToDisplay.length;
    if (remainingSlots > 0) {
        const availableImages = CONFIG.totalImages.filter(img => !imagesToDisplay.includes(img));
        const randomFillImages = shuffleArray(availableImages).slice(0, remainingSlots);
        imagesToDisplay.push(...randomFillImages);
    }

    // Shuffle the final array to mix user images and random images
    imagesToDisplay = shuffleArray(imagesToDisplay);

    // Populate the grid with mixed images
    imagesToDisplay.forEach((imgName) => {
        const imgElement = document.createElement('img');
        imgElement.src = CONFIG.imageDirectory + imgName;
        imgElement.classList.add('image-item');
        imgElement.dataset.index = imgName; // Store original name as data attribute
        imgElement.addEventListener('click', toggleImageSelection); // Add click handler
        imageGrid.appendChild(imgElement); // Add image to grid
    });

    console.log("Mixed images have been populated in the grid");
    
    // Handle case with no user images
    if (userImages.length === 0) {
        // Fall back to random images if no user images are found
        console.log("No user images found, showing random images");
        populateImageGrid();
    }
}

// Handle username input
document.getElementById('username').addEventListener('input', function(event) {
    const username = event.target.value.trim();
    if (username) {
        updateGridForUsername(username);
    } else {
        populateImageGrid(); // If the input is empty, show random images
    }
});

// Initialize the grid when DOM is loaded
document.addEventListener('DOMContentLoaded', populateImageGrid);

