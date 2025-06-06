import React from "react";

type ButtonProps = {
    text: string
    onClick?: React.MouseEventHandler<HTMLButtonElement> | undefined
    disabled: boolean
    testID?: string
}

export const Kiwi_Button: React.FC<ButtonProps> = (props: ButtonProps) => {
    return (
        <>
            <button
                type="button"
                role="button"
                onClick={props.onClick}
                disabled={props.disabled}
                className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                data-testid={props.testID}
            >
                {props.text}
            </button>
        </>
    )
};