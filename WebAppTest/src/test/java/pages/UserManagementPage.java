package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UserManagementPage {

    private WebDriver driver;

    // Locators
    private By nameInput = By.id("name");
    private By emailInput = By.id("email");
    private By roleInput = By.id("role");
    private By addUserButton = By.xpath("//button[text()='Add User']");
    private By firstUserName = By.xpath("//table//tr[2]/td[1]");
    private By firstUserRole = By.xpath("//table//tr[2]/td[3]");
    private By deleteButton = By.xpath("//button[text()='Delete']");

    // Constructor
    public UserManagementPage(WebDriver driver) {
        this.driver = driver;
    }

    // Actions
    public void enterName(String name) {
        driver.findElement(nameInput).sendKeys(name);
    }

    public void enterEmail(String email) {
        driver.findElement(emailInput).sendKeys(email);
    }

    public void enterRole(String role) {
        driver.findElement(roleInput).sendKeys(role);
    }

    public void clickAddUser() {
        driver.findElement(addUserButton).click();
    }

    public String getFirstUserName() {
        return driver.findElement(firstUserName).getText();
    }

    public String getFirstUserRole() {
        return driver.findElement(firstUserRole).getText();
    }

    public void deleteUser() {
        driver.findElement(deleteButton).click();
    }
}
