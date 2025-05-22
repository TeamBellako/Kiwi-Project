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
                className="mt-1 w-full border p-2 rounded"
                required={props.required}
                disabled={props.onChange === undefined}
            />
        </>
    )
};

type Kiwi_SensibleInputFieldProps = {
    inputFieldProps: InputFieldProps,
    trailingIconProps: TrailingSensibleInformationIconProps,
}
export const Kiwi_SensibleInputField: React.FC<Kiwi_SensibleInputFieldProps> = (props: Kiwi_SensibleInputFieldProps) => {
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
                    className="mt-1 w-full border p-2 rounded"
                    required={props.inputFieldProps.required}
                    disabled={props.inputFieldProps.onChange === undefined}
                />
                <Kiwi_TrailingSensibleInformationIcon {...props.trailingIconProps} />
            </div>
        </>
    )
};

