import styles from "./extra-info.module.css";

const ExtraInfo = ({children}) => {
    return (
        <div className="d-flex flex-row flex-nowrap gap-3 bg-body-secondary p-3 rounded-3">
            <i className={styles.informationIcon + " bi bi-info-circle"}></i>
            <div className={styles.textContainer + " d-flex flex-column gap-2"}>
                {children}
            </div>
        </div>
    )
}

export default ExtraInfo