import { useEffect, useState } from "react";
import "./toast.css";

const Toast = ({ message, type, onClose }) => {
    const [closing, setClosing] = useState(false);

    useEffect(() => {
        const showTimer = setTimeout(() => {
            setClosing(true);
        }, 5000);

        return () => clearTimeout(showTimer);
    }, []);

    useEffect(() => {
        if (!closing) return;

        const closeTimer = setTimeout(() => {
            onClose();
        }, 300); // duración de slideOut

        return () => clearTimeout(closeTimer);
    }, [closing, onClose]);

    return (
        <div className={`custom-toast ${type} ${closing ? "closing" : ""}`}>
            {message}
        </div>
    );
};

export default Toast;