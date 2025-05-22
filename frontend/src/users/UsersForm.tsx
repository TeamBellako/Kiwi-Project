import {UsersDTO} from "./UsersDTO";
import {useUsersForm} from "./UsersHook";
import React, {useState} from "react";
import {Kiwi_InputField, Kiwi_SensibleInputField} from "../ui/components/Kiwi_InputField";
import {Kiwi_Button} from "../ui/components/Kiwi_Button";
import {Kiwi_InfoBox} from "../ui/components/Kiwi_InfoBox";

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
            <Kiwi_InputField 
                label="Email"
                type="email"
                value={form.formState.email}
                onChange={(e: { target: { value: string; }; }) => form.updateFormState("email", e.target.value)}
                required={true}
            />

            <Kiwi_SensibleInputField
                inputFieldProps={{
                    label: "Password",
                    type: showPassword ? "text" : "password",
                    value: form.formState.password,
                    onChange: e => form.updateFormState("password", e.target.value),
                    required: true
                }}
                trailingIconProps={{
                    informationName: "password",
                    showInformation: showPassword,
                    setShowInformation: setShowPassword
                }}
            />

            <div className="flex space-x-4">
                <Kiwi_Button
                    text={"Signup"}
                    onClick={form.signupUser}
                    disabled={form.loading}
                />

                <Kiwi_Button
                    text={"Login"}
                    onClick={form.loginUser}
                    disabled={form.loading}
                />
            </div>
            
            <Kiwi_InfoBox
                text={form.error?.message}
                role={"error"}
                boxColor={"error"}
            />

            <Kiwi_InfoBox
                text={form.result}
                role={"result"}
                boxColor={"result"}
            />
        </form>
    );
};

export default UsersForm;
