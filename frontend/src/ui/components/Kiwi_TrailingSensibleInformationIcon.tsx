import React from "react";

export type TrailingSensibleInformationIconProps = {
    informationName: string;
    showInformation: boolean;
    setShowInformation: React.Dispatch<React.SetStateAction<boolean>>;
}

const Kiwi_TrailingSensibleInformationIcon: React.FC<TrailingSensibleInformationIconProps> = (props: TrailingSensibleInformationIconProps) => {
    return (
        <>
            <button
                type="button"
                tabIndex={-1}
                onMouseDown={() => props.setShowInformation(true)}
                onMouseUp={() => props.setShowInformation(false)}
                onMouseLeave={() => props.setShowInformation(false)}
                onTouchStart={() => props.setShowInformation(true)}
                onTouchEnd={() => props.setShowInformation(false)}
                className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                aria-label={props.showInformation ? "Hide " + props.informationName : "Show " + props.informationName}
            >
                {props.showInformation ? (
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
        </>
    )
};

export default Kiwi_TrailingSensibleInformationIcon;