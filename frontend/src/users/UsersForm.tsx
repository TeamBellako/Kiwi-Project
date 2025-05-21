import {UsersDTO} from "./UsersDTO";
import {useUsersForm} from "./UsersHook";
import React, {useState} from "react";

type UsersProps = Partial<UsersDTO>;

const UsersForm: React.FC<UsersProps> = (props: UsersProps) => {
    const form = useUsersForm(props);
    const [showPassword, setShowPassword] = useState(false);

    return (
        <form
            className="space-y-6"
            data-testid="users-form"
            onSubmit={e => e.preventDefault()}
        >
            <label htmlFor="email" className="block font-medium">
                Email
            </label>
            <input
                id="email"
                type="email"
                value={form.formState.email}
                onChange={e => form.updateFormState("email", e.target.value)}
                className="mt-1 w-full border p-2 rounded"
                required
            />

            <label htmlFor="password" className="block font-medium">
                Password
            </label>
            <div className="relative">
                <input
                    id="password"
                    type={showPassword ? "text" : "password"}
                    value={form.formState.password}
                    onChange={e => form.updateFormState("password", e.target.value)}
                    className="mt-1 w-full border p-2 rounded pr-10"
                    required
                />
                <button
                    type="button"
                    tabIndex={-1}
                    onMouseDown={() => setShowPassword(true)}
                    onMouseUp={() => setShowPassword(false)}
                    onMouseLeave={() => setShowPassword(false)}
                    onTouchStart={() => setShowPassword(true)}
                    onTouchEnd={() => setShowPassword(false)}
                    className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                    aria-label={showPassword ? "Hide password" : "Show password"}
                >
                    {/* Simple eye icon SVG */}
                    {showPassword ? (
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                            <path strokeLinecap="round" strokeLinejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-5.523 0-10-4.477-10-10 0-.27.02-.536.058-.796m1.11-1.908A9.958 9.958 0 0112 5c5.523 0 10 4.477 10 10 0 .27-.02.536-.058.796m-7.47-7.47a3 3 0 114.243 4.243m-6.364 1.06a3 3 0 114.242-4.243" />
                        </svg>
                    ) : (
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                            <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                            <path strokeLinecap="round" strokeLinejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.477 0 8.268 2.943 9.542 7-1.274 4.057-5.065 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                        </svg>
                    )}
                </button>
            </div>

            <div className="flex space-x-4">
                <button
                    type="button"
                    onClick={form.signupUser}
                    disabled={form.loading}
                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                >
                    Signup
                </button>

                <button
                    type="button"
                    onClick={form.loginUser}
                    disabled={form.loading}
                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                >
                    Login
                </button>
            </div>

            {form.error && (
                <p className="mt-2 text-red-600" role="error" style={{ whiteSpace: "pre-wrap" }}>
                    Error: {form.error.message}
                </p>
            )}

            {form.result && (
                <p className="mt-2 text-green-600" role="result">
                    Result: {form.result}
                </p>
            )}
        </form>
    );
};

export default UsersForm;
