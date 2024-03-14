import PageObject.SeleniumGroovyPO
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.WebDriverWait

import java.time.Duration

/*
    1. Go to https://www.webstaurantstore.com/
    2. Search for 'stainless work table'.
    3. Check the search result ensuring every product has the word 'Table' in its title.
    4. Add the last of found items to Cart.
    5. Empty Cart.
 */

class SeleniumGroovyTestCase {
    WebDriver driver // Creates WebDriver Object
    WebDriverWait wd_wait //WebDriver Wait Object

   // def locator = new WebStaurantLocators()

    @Before
    void StartUp() {
        /*
        I decided not to run this in headless mode, because then you can see what it is doing :)
         */
        driver = new ChromeDriver()
        wd_wait = new WebDriverWait(driver, Duration.ofSeconds(30))
        driver.manage().window().maximize()
        driver.get('https://www.webstaurantstore.com/')
    }

    @Test
    void getAllProducts_AddToCartLast_DeleteFromCart_Test() {
        //Create locatorsObject
        SeleniumGroovyPO pageObject = new SeleniumGroovyPO(driver, wd_wait)

        pageObject.querySearchBar()
        pageObject.getAllProducts()
        pageObject.addToCart()
        pageObject.viewCart()
        pageObject.deleteCartItem()

    }


}
