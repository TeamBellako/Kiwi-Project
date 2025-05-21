export class Password {
    private static readonly PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

    private constructor(public readonly value: string) {}

    static isValid(password: string): boolean {
        return this.PASSWORD_REGEX.test(password);
    }

    static of(password: string): Password {
        if (!this.isValid(password)) {
            throw new Error(
                "Invalid password, passwords must:\n" +
                "- Be at least 8 characters long\n" +
                "- Include both uppercase and lowercase letters\n" +
                "- Contain at least one number\n" +
                "- Contain at least one special character"
            );
        }
        return new Password(password);
    }

}