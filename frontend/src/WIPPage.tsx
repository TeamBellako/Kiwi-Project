import React from "react";

const WIPPage: React.FC = () => {
    return (
        <div className={'relative flex flex-col w-screen h-screen items-center justify-center bg-[#122F36] text-[#FCFCF6]'}>
            <img alt={'background image'} src={'/background.png'} className={'absolute inset-0 w-full h-full object-cover'} />
            <h1 className={'text-4xl font-bold z-10'}>Welcome to GrowTale</h1>
            <h2 className={'text-2xl mb-6 z-10'}>Your Legend Is About To Be Forged...</h2>
            <a
                href={''}
                className={'bg-[#122F36] text-white rounded-full py-3 px-12 text-center text-lg font-semibold transition-all duration-300 transform hover:bg-[#266270] active:scale-95 z-10'}>
                Let's Do It
            </a>

            <div className={'absolute bottom-6 left-1/2 transform -translate-x-1/2 flex flex-row items-center gap-2 z-10'}>
                <h3 className={'text-sm text-[#D4DCC1]'}>Bellako Tech © 2025</h3>
            </div>
        </div>
    )
};

export default WIPPage