import React from "react";

type SliderProps = {
    text?: string;
    label?: string;
    min: number;
    max: number;
    step?: number;
    value?: string | number | readonly string[] | undefined
    onChange?: React.ChangeEventHandler<HTMLInputElement> | undefined
    testID?: string;
}

export const Kiwi_Slider: React.FC<SliderProps> = (props: SliderProps) => {
    return (
        <>
            <div className="flex flex-col gap-2">
                <label htmlFor={props.label} className="font-medium">
                    {props.text}
                </label>
                <input
                    id={props.label}
                    type="range"
                    role="slider"
                    min={props.min}
                    max={props.max}
                    step={props.step}
                    value={props.value}
                    onChange={props.onChange}
                    className="w-full"
                    data-testid={props.testID}
                />
            </div>
        </>
    )
};