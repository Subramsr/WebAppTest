function addUser() {
    let nameInput = document.getElementById("name"); // locator for name
    let emailInput = document.getElementById("email"); // locator for email
    let roleInput = document.getElementById("role"); // locator for role
    let name = nameInput.value;
    let email = emailInput.value;
    let role = roleInput.value;

    // Sample usage of locator:
    // emailInput.style.backgroundColor = "#e0f7fa";

    if (name === "" || email === "" || role === "") {
        alert("Please enter all details");
        return;
    }

    let table = document.getElementById("userTable");
    let row = table.insertRow();

    row.insertCell(0).innerHTML = name;
    row.insertCell(1).innerHTML = email;
    row.insertCell(2).innerHTML = role;
    row.insertCell(3).innerHTML =
        '<button onclick="deleteUser(this)">Delete</button>';

    nameInput.value = "";
    emailInput.value = "";
    roleInput.value = "";
}

function deleteUser(btn) {
    let row = btn.parentNode.parentNode;
    row.parentNode.removeChild(row);
}
