import { Route, Routes } from 'react-router-dom';
import Home from "./views/public/home/home"
import Layout from './components/layouts/layout/layout';
import MisFiguritas from "./views/public/mis-figuritas/mis-figuritas.jsx";
import Sugerencias from "./views/public/sugerencias/sugerencias.jsx";
import VerSubasta from "./views/public/ver-subasta/ver-subasta.jsx";

const publics = [
    {
        path: '/',
        element: <Home />,
    },
    {
        path: '/mis-figuritas',
        element: <MisFiguritas />
    },
    {
        path: '/sugerencias',
        element: <Sugerencias />
    },
    {
        path: '/subastas/:subId',
        element: <VerSubasta />
    }
];

const privates = [];

const AppRoutes = () => {
  return (
    <Routes>
      <Route element={<Layout />}>
        {publics.map(route => (
          <Route key={route.path} path={route.path} element={route.element} />
        ))}
        {privates.map(route => (
          <Route key={route.path} path={route.path} element={route.element} />
        ))}
      </Route>
      {/* <Route path="*" element={<NotFound />} /> */}
    </Routes>
  );
};

export default AppRoutes;