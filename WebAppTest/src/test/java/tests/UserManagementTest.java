package tests;

import base.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import pages.UserManagementPage;

public class UserManagementTest extends BaseTest {

    @Test
    public void testAddAndDeleteUser() throws InterruptedException {

        UserManagementPage userPage = new UserManagementPage(driver);

        userPage.enterName("John");
        userPage.enterEmail("john@gmail.com");
        userPage.enterRole("Admin");
        userPage.clickAddUser();

        Thread.sleep(2000);

        Assert.assertEquals("John", userPage.getFirstUserName());
        Assert.assertEquals("Admin", userPage.getFirstUserRole());
        System.out.println("User added successfully ✅");

        userPage.deleteUser();
        Thread.sleep(2000);
        System.out.println("User deleted successfully ✅");
    }

    @Test
    public void testNameFieldHoverTooltip() {
        // Move to the Name input and check the tooltip
        org.openqa.selenium.WebElement nameInput = driver.findElement(org.openqa.selenium.By.id("name"));
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        actions.moveToElement(nameInput).perform();
        // Get the title attribute (tooltip)
        String tooltip = nameInput.getAttribute("title");
        Assert.assertEquals("Provide name", tooltip);
        System.out.println("Tooltip for Name field is correctly displayed: " + tooltip);
    }
}
