import { Outlet } from 'react-router-dom';
import styles from './layout.module.css';
import Navbar from '../navbar/navbar';

const Layout = () => {
  return (
    <>
      <Navbar />
      <main className={styles.main}>
        <Outlet />
      </main>
    </>
  );
};

export default Layout;