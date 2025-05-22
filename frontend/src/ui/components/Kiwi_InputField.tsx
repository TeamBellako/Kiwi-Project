import React from "react";
import Kiwi_TrailingSensibleInformationIcon, {
    TrailingSensibleInformationIconProps
} from "./Kiwi_TrailingSensibleInformationIcon";

type InputFieldProps = {
    label?: string;
    type?: string | undefined;
    value: string | number | readonly string[] | undefined
    onChange?: React.ChangeEventHandler<HTMLInputElement> | undefined
    required: boolean;
}

export const Kiwi_InputField: React.FC<InputFieldProps> = (props: InputFieldProps) => {
    const isDisabled = props.onChange === undefined;

    return (
        <>
            <label htmlFor={props.label?.toLowerCase()} className="block font-medium">
                {props.label || ""}
            </label>
            <input
                id={props.label?.toLowerCase()}
                type={props.type?.toLowerCase()}
                value={props.value}
                onChange={props.onChange}
                className={`mt-1 w-full border p-2 rounded ${isDisabled ? "opacity-50 cursor-not-allowed" : ""}`}
                required={props.required}
                disabled={isDisabled}
            />
        </>
    );
};


type Kiwi_SensibleInputFieldProps = {
    inputFieldProps: InputFieldProps,
    trailingIconProps: TrailingSensibleInformationIconProps,
}

export const Kiwi_SensibleInputField: React.FC<Kiwi_SensibleInputFieldProps> = (props: Kiwi_SensibleInputFieldProps) => {
    const isDisabled = props.inputFieldProps.onChange === undefined;

    return (
        <>
            <label htmlFor={props.inputFieldProps.label?.toLowerCase()} className="block font-medium">
                {props.inputFieldProps.label || ""}
            </label>
            <div className="relative">
                <input
                    id={props.inputFieldProps.label?.toLowerCase()}
                    type={props.inputFieldProps.type?.toLowerCase()}
                    value={props.inputFieldProps.value}
                    onChange={props.inputFieldProps.onChange}
                    className={`mt-1 w-full border p-2 rounded ${isDisabled ? "opacity-50 cursor-not-allowed" : ""}`}
                    required={props.inputFieldProps.required}
                    disabled={isDisabled}
                />
                <Kiwi_TrailingSensibleInformationIcon {...props.trailingIconProps} />
            </div>
        </>
    );
};