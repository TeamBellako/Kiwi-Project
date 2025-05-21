export class Password {
    private static readonly PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

    private constructor(public readonly value: string) {}

    static isValid(password: string): boolean {
        return this.PASSWORD_REGEX.test(password);
    }

    static of(password: string): Password {
        if (!this.isValid(password)) {
            throw new Error("Invalid password format");
        }
        return new Password(password);
    }
}