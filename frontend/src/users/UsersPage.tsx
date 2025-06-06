import {UsersDTO} from "./UsersDTO";
import {useUsersForm} from "./UsersHook";
import React, {useState} from "react";
import {Kiwi_InputField, Kiwi_SensibleInputField} from "../ui/components/Kiwi_InputField";
import {Kiwi_Button} from "../ui/components/Kiwi_Button";
import {Kiwi_InfoBox} from "../ui/components/Kiwi_InfoBox";
import {useAppSelector} from "../store/Hooks";
import {selectUsersError, selectUsersStatus} from "./UsersSelector";
import LoadingPage from "../common/LoadingPage";
import ErrorPage from "../common/ErrorPage";
import {TestIDs} from "../common/TestIDs";

type UsersProps = Partial<UsersDTO>;

const UsersPage: React.FC<UsersProps> = (props: UsersProps) => {
    const status = useAppSelector(selectUsersStatus)
    const error = useAppSelector(selectUsersError)
    
    const form = useUsersForm(props);
    const [showPassword, setShowPassword] = useState(false);
    

    if (status === 'loading') {
        return (
            <LoadingPage />
        );
    }

    if (status === 'failed' && error?.code && error.code >= 500) {
        return (
            <ErrorPage
                onRetry={form.handleRetry}
            />
        );
    }

    return (
        <div className="p-4 max-w-lg mx-auto">
            <h1 className="text-2xl font-bold mb-4">Welcome</h1>
            <form
                role="form"
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
                    testID={TestIDs.users.email}
                />
    
                <Kiwi_SensibleInputField
                    inputFieldProps={{
                        label: "Password",
                        type: showPassword ? "text" : "password",
                        value: form.formState.password,
                        onChange: e => form.updateFormState("password", e.target.value),
                        required: true,
                        testID: TestIDs.users.password
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
                        testID={TestIDs.users.signup}
                    />
    
                    <Kiwi_Button
                        text={"Login"}
                        onClick={form.loginUser}
                        disabled={form.loading}
                        testID={TestIDs.users.login}
                    />
                </div>
                
                <Kiwi_InfoBox
                    text={form.error?.message}
                    boxColor={"error"}
                    testID={TestIDs.users.errorAlert}
                />
    
                <Kiwi_InfoBox
                    text={form.result}
                    boxColor={"result"}
                    testID={TestIDs.users.resultAlert}
                />
            </form>
        </div>
    );
};

export default UsersPage;
