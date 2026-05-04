import { Route, Routes } from 'react-router-dom';
import Home from "./views/public/home/home"
import Layout from './components/layouts/layout/layout';
import MisFiguritas from "./views/public/mis-figuritas/mis-figuritas.jsx";
import NuevaFaltante from "./views/public/nueva-faltante/nueva-faltante.jsx";
import NuevaRepetida from "./views/public/nueva-repetida/nueva-repetida.jsx";
import Perfil from "./views/public/perfil/perfil.jsx";


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
        path: '/mis-figuritas/nueva-faltante',
        element: <NuevaFaltante />
    },
    {
        path: '/mis-figuritas/nueva-repetida',
        element: <NuevaRepetida />
    },
    {
        path: '/perfil',
        element: <Perfil />
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