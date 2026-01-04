package com.kiwi.features.users.data;

import com.kiwi.common.types.Email;
import com.kiwi.common.types.PositiveOrZeroInteger;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Setter
@Getter
public class UsersDomain {
    private Email email;
    private LocalDate registerDate;
    private PositiveOrZeroInteger currentPoints;
    private PositiveOrZeroInteger totalPoints;

    public UsersDomain(Email email, LocalDate registerDate) {
        this.email = email;
        this.registerDate = registerDate;
        this.currentPoints = new PositiveOrZeroInteger(0);
        this.totalPoints = new PositiveOrZeroInteger(0);
    }

    public UsersDomain(Email email, LocalDate registerDate, PositiveOrZeroInteger currentPoints, PositiveOrZeroInteger totalPoints) {
        this.email = email;
        this.registerDate = registerDate;
        this.currentPoints = currentPoints;
        this.totalPoints = totalPoints;
    }

    public void addPoints(PositiveOrZeroInteger pointsToAdd) {
        this.currentPoints = this.currentPoints.add(pointsToAdd);
        this.totalPoints = this.totalPoints.add(pointsToAdd);
    }

    public void subtractCurrentPoints(PositiveOrZeroInteger pointsToSubtract) {
        this.currentPoints = this.currentPoints.subtract(pointsToSubtract);
    }

    public void resetCurrentPoints() {
        this.currentPoints = new PositiveOrZeroInteger(0);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsersDomain other = (UsersDomain) o;
        return Objects.equals(email, other.email) &&
                Objects.equals(registerDate, other.registerDate) &&
                Objects.equals(currentPoints, other.currentPoints) &&
                Objects.equals(totalPoints, other.totalPoints);
    }

}
