type Props = {
    value: string;
    setValue: (value: string) => void;
    error: string;
    setError: (error: string) => void;
    validate: () => void;
};

const EmailField: React.FC<Props> = ({ value, setValue, error, validate }) => {
    return (
        <div>
            <label htmlFor="email" className="block font-medium">
                Email
            </label>
            <input
                id="email"
                type="email"
                value={value}
                onChange={(e) => setValue(e.target.value)}
                onBlur={validate}
                className="mt-1 w-full border p-2 rounded"
            />
            {error && (
                <label className="text-red-500 mt-1" role="alert">
                    {error}
                </label>
            )}
        </div>
    );
};

export default EmailField;
