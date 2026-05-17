import { useEffect, useState } from "react";
import "./toast.css";

const Toast = ({ message, type, onClose }) => {
    const [closing, setClosing] = useState(false);

    useEffect(() => {
        const timer = setTimeout(() => {
            setClosing(true);
        }, 5000);

        return () => clearTimeout(timer);
    }, []);

    const handleAnimationEnd = () => {
        if (closing) {
            onClose();
        }
    };

    return (
        <div
            className={`toast ${type} ${closing ? "closing" : ""}`}
            onAnimationEnd={handleAnimationEnd}
        >
            {message}
        </div>
    );
};

export default Toast;