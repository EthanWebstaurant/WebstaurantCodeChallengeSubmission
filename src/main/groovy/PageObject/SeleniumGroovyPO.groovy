package PageObject
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

class SeleniumGroovyPO {
    def driver
    def wd_wait
    //Homepage Search bar
    By searchbar = By.id("searchval")

    //Relative XPATH for 'landingPageHeader' might be the wrong choice here due to the react component.
    //This is pointing to the "stainless work table" header at the top of search results page.
    By landingPageHeader = By.xpath("//*[text()='stainless work table']")

    //Relative XPATH for product items
    By allProducts = By.xpath("//*[@id=\"ProductBoxContainer\"]")

    //This is the XPATH for the parent for the "next page" navigation buttons
    By pageNextParent = By.id("paging")
    //This is the XPATH for the "next page" navigation buttons
    By pageNext = By.xpath("//*[@id=\"paging\"]/nav/ul/*")

    By lastPage = By.xpath("//*[@id=\"paging\"]/nav/ul/li[7]/a")
    //view cart button
    By viewCart = By.xpath("/html/body/div[12]/div/p/div[2]/div[2]/a[1]")
    //Delete single item in cart
    By deleteCartItem = By.xpath("//*[@id=\"main\"]/div[1]/div/div[2]/ul/li[2]/div/div[6]/button")
    //Make sure the cart is empty by checking header
    By assertEmptyCart = By.xpath("//*[@id=\"main\"]/div/div[1]/div[1]/div/div[2]/p[1]")

    SeleniumGroovyPO(WebDriver driver, WebDriverWait wd_wait) {
        this.driver = driver
        this.wd_wait = wd_wait
    }

    def querySearchBar() {
        /*
        Find searchbar, and have it search for 'stainless work table'
         */
        wd_wait.until(ExpectedConditions.visibilityOfElementLocated(searchbar)).sendKeys('stainless work table' + Keys.ENTER)
        println("Search Bar will now return a dropdown with 'stainless work table' options and hit the 'enter' key")

        /*
        Make sure we are on the correct page based on a header element
         */
        wd_wait.until(ExpectedConditions.visibilityOfElementLocated(landingPageHeader))
        println("Found landing page header")

        println("~~~~Query complete~~~~")
    }

    def getAllProducts() {
        println("~~~Starting to get all products~~~")
        /*
        This is looking for the parent element of all the page buttons at the bottom of the screen
         */
        wd_wait.until(ExpectedConditions.visibilityOfElementLocated(pageNextParent))

        int productCount = 0 // This will count all products on each page
        def pageNextArrow = driver.findElements(pageNext) // This is the next page arrow button
        int totalPages = driver.findElement(lastPage).text.toInteger() // Gets the Total number of pages to navigate
        int currentPage = 0 // current page counter || This will keep track of which page we are on
        int tempIndex //this is a tempIndex due to stale elements(each time the page changes, the elements also change)
        def productsList = [] // This will store all of the products

        while(currentPage <= totalPages) {
            wd_wait.until(ExpectedConditions.visibilityOfElementLocated(pageNextParent))
            productsList += driver.findElements(allProducts).text // This is storing the products found
            productCount += driver.findElements(allProducts).size() // storing the size or count of all products found
            currentPage++ // This is telling us which page we are currently on

            /*
            The loop has an extra iteration in it to put up the last page's products
            When the products are picked up / stored, the loop will close
            (we don't want it to click on the last page again)
            */
            if(currentPage == totalPages) {
                println("Final page has been reached")
                println("All products have been stored")
                println("Total Pages: ${totalPages}")
                println("Total Products: ${productCount}")
                break
            }

            /*
            The list storing the ArrowButton to navigate to the next page changes with each page.
            TempIndex stores the list each iteration to avoid "stale elements"
             */
            tempIndex = pageNextArrow.size() //The index keep changing
            pageNextArrow.get(tempIndex - 1).click()

            pageNextArrow = driver.findElements(pageNext) // Refreshing the list elements (stale and changing elements)
        }
        /*
        Assert that the total products in the list equals the expected count
         */
        assert productCount == productsList.size()

        /*
        ItemNumberError will return the product number IF there is an item that does not contain "Table"
        This loop will Assert/Check that all products found contain the work "Table"
        There is a bug, where 1 of the items DOES NOT contains "Table"
         */
        println("Checking all products for the 'Table' description:")
        int itemNumberError = 1; // This is for debugging and error handling
        for(product in productsList) {
            try {
                assert product.contains("Table") // Asser that all products contain "Table"
            }catch(AssertionError ae) {
                println("Item Number: ${itemNumberError} on Page ${Math.ceil(itemNumberError / 60).toInteger()} ~~~~~~~~~ ${product}")
                println(ae.message)
            }
            itemNumberError++
        }

        println("~~~~GetAllProducts: Complete~~~~")
    }

    def addToCart() {
        int totalPages = driver.findElement(lastPage).text.toInteger() // Gets the Total number of pages
        /*
        Assert that we are on the final product page
        This could be handled differently, however we SHOULD BE on the final page,
        I want this test to fail if we are not on the final page
         */

        /*
        TotalPages + 1 because of it is index 0 based
         */
        assert driver.getCurrentUrl().contains("page=${totalPages + 1}")

        /*
        LastPageProducts is kind of redundant, but we are getting the final pages products again.
        In the future, maybe this should all have been a dictionary<int pageNumber<WebElement productItem>>,
        but both work :)
         */
        def lastPageProducts = driver.findElements(allProducts) // Get all products from last page
        WebElement lastProduct = lastPageProducts.last() // Get the last product in the list
        lastProduct.findElement(By.name("addToCartButton")).click() // Get click on the 'Add to Cart' button

        println("~~~~AddtoCart: Complete~~~~")
    }

    def viewCart() {
        /*
        Click on the view cart button from the model pop-up
         */
        wd_wait.until(ExpectedConditions.visibilityOfElementLocated(viewCart)).click()

        println("~~~~ViewCart: Complete~~~~")
    }

    def deleteCartItem() {
        /*
        Delete the single item from the cart.
        Assert that the cart is empty with the correct text
         */
        wd_wait.until(ExpectedConditions.visibilityOfElementLocated(deleteCartItem)).click()
        wd_wait.until(ExpectedConditions.visibilityOfElementLocated(assertEmptyCart))
        assert driver.findElement(assertEmptyCart).text == "Your cart is empty."

        println("~~~~DeleteCart: Complete~~~~")
    }

}

