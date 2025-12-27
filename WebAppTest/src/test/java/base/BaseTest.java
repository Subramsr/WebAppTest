package base;

import org.openqa.selenium.WebDriver;
import org.junit.After;
import org.junit.Before;
import utils.DriverFactory;

public class BaseTest {

    protected WebDriver driver;

    @Before
    public void setUp() {
        driver = DriverFactory.initDriver();
        driver.get("file:///C:\\Users\\sridh\\OneDrive\\Desktop\\Sample Application\\index.html");
    }

    @After
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
