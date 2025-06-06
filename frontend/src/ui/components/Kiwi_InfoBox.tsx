import React from "react";

type InfoBoxProps = {
    text?: string | null;
    boxColor: "result" | "error";
    testID?: string;
};

const BOX_COLOR_CLASSES: Record<string, { bg: string; border: string; text: string }> = {
    result: {
        bg: 'bg-green-100',       
        border: 'border-green-500',
        text: 'text-black',      
    },
    error: {
        bg: 'bg-red-600',        
        border: 'border-red-600', 
        text: 'text-white',      
    },
};

export const Kiwi_InfoBox: React.FC<InfoBoxProps> = (props: InfoBoxProps) => {
    if (!props.text) return null;

    const colors = BOX_COLOR_CLASSES[props.boxColor];

    return (
        <div className={`mt-2 p-3 rounded border-l-4 ${colors.bg} ${colors.border}`}>
            <p 
                className={`${colors.text}`}
                role="alert"
                style={{ whiteSpace: "pre-wrap" }}
                data-testid={props.testID}
            >
                {props.text}
            </p>
        </div>
    );
};
