package hr.tvz.java.freelance.freelancemanagementtool.repository;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * A simple utility class to generate a BCrypt hash for a given password.
 * Run this class's main method to get a hash string.
 */
public class HashGenerator {

    public static void main(String[] args) {
        // --- CHANGE THE PASSWORD HERE ---
        String myPassword = "password";
        // --------------------------------

        // The BCrypt library creates a secure hash. The number '12' is the "cost factor",
        // which controls how slow (and thus secure) the hashing is. 12 is a good default.
        String generatedHash = BCrypt.withDefaults().hashToString(12, myPassword.toCharArray());

        System.out.println("Password to hash: " + myPassword);
        System.out.println("--- COPY THE LINE BELOW ---");
        System.out.println(generatedHash);
        System.out.println("---------------------------");
    }
}