import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierCreateTest {
    static private String loginTest = new Faker().name().username();
    static private String passwordTest = new Faker().internet().password();
    static private String firstNameTest = new Faker().name().firstName();
    static private String endPointCreate = "/api/v1/courier";
    static private String endPointLogin = "/api/v1/courier/login";
    static private String endPointDelete = "/api/v1/courier/";

    @Before
    public void setUp() throws IOException {

        Properties prop = new Properties();
        InputStream input = new FileInputStream("src/main/resources/config.properties");
        prop.load(input);
        RestAssured.baseURI = prop.getProperty("baseURI");
    }

    @After
    public void tearDown() {
        deleteCourierProfile();
    }

    @Step("Delete courier profile")
    public void deleteCourierProfile() {
        CourierProfile courierDelete  = new CourierProfile(loginTest, passwordTest);
        String response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courierDelete)
                .when()
                .post(endPointLogin)
                .asString();

        JsonPath jsonPath = new JsonPath(response);
        String userId = jsonPath.getString("id");
        delete(endPointDelete + userId);
    }

    @Test
    @DisplayName("Создание нового курьера с корректными данными")
    @Description("/api/v1/courier post: login, password, firstName")
    public void createNewCourierAndCheckResponse(){
        CourierProfile courierCreate  = new CourierProfile(loginTest, passwordTest, firstNameTest);
        createCourierProfile(courierCreate, 201, true);
    }

    @Test
    @DisplayName("Создание нового курьера с пустым полем firstName")
    @Description("/api/v1/courier post: login, password")
    public  void createCourierWithoutFirstName() {
        CourierProfile courierCreate  = new CourierProfile(loginTest, passwordTest);
        createCourierProfile(courierCreate, 400, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    @Description("/api/v1/courier post: login, password, firstName")
    public void createCourierWithSameLoginAndCheckResponse(){
        CourierProfile courierCreate  = new CourierProfile(loginTest, passwordTest, firstNameTest);
        createCourierProfile(courierCreate, 201, true);
        createCourierProfile(courierCreate, 409, "Этот логин уже используется. Попробуйте другой.");
    }

    @Step("Create courier profile with {courierCreate}")
    public void createCourierProfile(CourierProfile courierCreate, int statusCode, Object body) {
        given()
                .header("Content-type","application/json")
                .and()
                .body(courierCreate)
                .when()
                .post(endPointCreate)
                .then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));
// Проверяем, что нельзя создать такого же курьера:
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courierCreate)
                .when()
                .post(endPointCreate)
                .then().assertThat().statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой.")); // Actual: Этот логин уже используется. Попробуйте другой.
    }
}
