package hr.tvz.java.freelance.freelancemanagementtool.repository;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordTester {

    public static void main(String[] args) {
        // This is the hash from our users.txt file
        String a_stored_hash = "$2a$12$efXwKax9zPIOUCWyyRmGPueenQds1xy/fLMXLYsfB/VF6eKM3X4Nu";

        // This is the password we are testing
        String password_to_check = "password";

        // The BCrypt library does the verification
        BCrypt.Result result = BCrypt.verifyer().verify(password_to_check.toCharArray(), a_stored_hash);

        // Print the result
        if (result.verified) {
            System.out.println("The password 'password' MATCHES the hash!");
        } else {
            System.out.println("The password 'password' DOES NOT MATCH the hash.");
        }

        // Let's try with a wrong password to see the difference
        String wrong_password = "Password"; // With a capital P
        BCrypt.Result wrong_result = BCrypt.verifyer().verify(wrong_password.toCharArray(), a_stored_hash);

        if (wrong_result.verified) {
            System.out.println("The password 'Password' MATCHES the hash!");
        } else {
            System.out.println("The password 'Password' DOES NOT MATCH the hash. This is expected.");
        }
    }
}