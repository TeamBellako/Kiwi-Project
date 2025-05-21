import {UsersDTO} from "./UsersDTO";
import {useUsersForm} from "./UsersHook";
import React from "react";

type UsersProps = Partial<UsersDTO>;

const UsersForm: React.FC<UsersProps> = (props: UsersProps) => {
    const form = useUsersForm(props);

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
            <input
                id="password"
                type="password"
                value={form.formState.password}
                onChange={e => form.updateFormState("password", e.target.value)}
                className="mt-1 w-full border p-2 rounded"
                required
            />

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
                <p className="mt-2 text-red-600" role="alert" style={{ whiteSpace: "pre-wrap" }}>
                    Error: {form.error.message}
                </p>
            )}

            {form.result && (
                <p className="mt-2 text-green-600" role="status">
                    {form.result}
                </p>
            )}
        </form>
    );
};

export default UsersForm;