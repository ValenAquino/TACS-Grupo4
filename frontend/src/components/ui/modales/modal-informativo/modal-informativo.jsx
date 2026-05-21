import styles from "./modal-informativo.module.css"
import {Spinner} from "../../spinner/spinner.jsx";

const ModalInformativo = ({ open, children }) => {

    if (!open) return null

    return (
        <div className={styles.overlay}>
            <div className={styles.modal}>
                {children}
                <Spinner />
            </div>
        </div>
    )
}

export default ModalInformativo