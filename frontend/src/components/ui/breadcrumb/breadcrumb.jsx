import styles from './breadcrumb.module.css';
import {Link} from "react-router-dom";

const Breadcrumb = ({crumbs = []}) => {
    return (
        <ol className="breadcrumb m-0 fs-6">

            {crumbs.map((c,index) =>
                <li key={index} className={" breadcrumb-item " + (crumbs.length === index+1 ? "active" : "")}>
                    <Link className={styles.crumb + " text-decoration-none  text-dark rounded-3 p-2"} to={c.to}>{c.name}</Link>
                </li>)
            }
        </ol>
    )
}

export default Breadcrumb