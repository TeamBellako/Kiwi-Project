package com.kiwi.features.users.data;

import com.kiwi.common.types.Email;
import com.kiwi.common.types.Password;
import com.kiwi.common.types.PositiveOrZeroInteger;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Setter
@Getter
public class UsersDomain {
    private Email email;
    private Password password;
    private LocalDate registerDate;
    private PositiveOrZeroInteger currentPoints;
    private PositiveOrZeroInteger totalPoints;

    public UsersDomain(Email email, Password password, LocalDate registerDate) {
        this.email = email;
        this.password = password;
        this.registerDate = registerDate;
        this.currentPoints = new PositiveOrZeroInteger(0);
        this.totalPoints = new PositiveOrZeroInteger(0);
    }

    UsersDomain(Email email, Password password, LocalDate registerDate, PositiveOrZeroInteger currentPoints, PositiveOrZeroInteger totalPoints) {
        this.email = email;
        this.password = password;
        this.registerDate = registerDate;
        this.currentPoints = currentPoints;
        this.totalPoints = totalPoints;
    }

    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }

    public Password getPassword() { return password; }
    public void setPassword(Password password) { this.password = password; }


    public LocalDate getRegisterDate() { return registerDate; }
    public void setRegisterDate(LocalDate registerDate) { this.registerDate = registerDate; }

    public int getCurrentPoints() { return currentPoints.value(); }
    public int getTotalPoints() { return totalPoints.value(); }

    // Métodos internos del backend para gestionar puntos
    public void addPoints(Integer pointsToAdd) {
        if (pointsToAdd == null || pointsToAdd <= 0) {
            throw new IllegalArgumentException("Points to add must be a positive number");
        }
        
        long newCurrent = (long) this.currentPoints.value() + pointsToAdd;
        long newTotal = (long) this.totalPoints.value() + pointsToAdd;
        
        if (newCurrent > Integer.MAX_VALUE || newTotal > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Points overflow: value exceeds maximum allowed");
        }
        
        this.currentPoints = new PositiveOrZeroInteger((int) newCurrent);
        this.totalPoints = new PositiveOrZeroInteger((int) newTotal);
    }

    public void subtractCurrentPoints(Integer pointsToSubtract) {
        if (pointsToSubtract == null || pointsToSubtract <= 0) {
            throw new IllegalArgumentException("Points to subtract must be a positive number");
        }
        
        int newCurrentPoints = Math.max(0, this.currentPoints.value() - pointsToSubtract);
        this.currentPoints = new PositiveOrZeroInteger(newCurrentPoints);
    }

    public void resetCurrentPoints() {
        this.currentPoints = new PositiveOrZeroInteger(0);
    }

    void setCurrentPointsInternal(Integer points) {
        this.currentPoints = new PositiveOrZeroInteger(points);
    }

    void setTotalPointsInternal(Integer points) {
        this.totalPoints = new PositiveOrZeroInteger(points);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsersDomain other = (UsersDomain) o;
        return Objects.equals(email, other.email) &&
                Objects.equals(password, other.password) &&
                Objects.equals(registerDate, other.registerDate) &&
                Objects.equals(currentPoints, other.currentPoints) &&
                Objects.equals(totalPoints, other.totalPoints);
    }

}
