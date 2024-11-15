package model;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

public class APIclient {
    private static final String BASE_URL = "http://94.198.50.185:7081/api/users";
    private RestTemplate restTemplate;
    private String sessionId;

    public APIclient() {
        restTemplate = new RestTemplate();
    }

    public void run() {
        // 1. Получить список всех пользователей
        ResponseEntity<List<User>> response = restTemplate.exchange(BASE_URL, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<User>>() {});
        List<User> users = response.getBody();
        System.out.println(users);
        sessionId = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        // 2. Сохранить пользователя
        User newUser = new User();
        newUser.setId(3);
        newUser.setName("James");
        newUser.setLastName("Brown");
        newUser.setAge((byte) 25); // Ваш выбор возраста

        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, Collections.singletonList(sessionId));
        HttpEntity<User> requestEntity = new HttpEntity<>(newUser, headers);

        ResponseEntity<String> addUserResponse = restTemplate.postForEntity(BASE_URL, requestEntity, String.class);
        String firstPartCode = addUserResponse.getBody();
        System.out.println(newUser);
        // 3. Изменить пользователя
        newUser.setName("Thomas");
        newUser.setLastName("Shelby");

        HttpEntity<User> updateUserEntity = new HttpEntity<>(newUser, headers);
        ResponseEntity<String> updateUserResponse = restTemplate.exchange(BASE_URL, HttpMethod.PUT, updateUserEntity, String.class);
        String secondPartCode = updateUserResponse.getBody();
        System.out.println(newUser);
        // 4. Удалить пользователя
        ResponseEntity<String> deleteUserResponse = restTemplate.exchange(BASE_URL + "/3", HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        String thirdPartCode = deleteUserResponse.getBody();

        // Объединение всех частей кода
        String finalCode = firstPartCode + secondPartCode + thirdPartCode;
        System.out.println("Final code: " + finalCode);
    }

    public static void main(String[] args) {
        APIclient client = new APIclient();
        client.run();
    }
}