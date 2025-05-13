export class Email {
    private static readonly EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    private constructor(public readonly value: string) {}

    static isValid(email: string): boolean {
        return this.EMAIL_REGEX.test(email);
    }

    static of(email: string): Email {
        if (!this.isValid(email)) {
            throw new Error("Invalid email format");
        }
        return new Email(email);
    }
}