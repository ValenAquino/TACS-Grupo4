import { Route, Routes } from 'react-router-dom';
import Home from "./views/public/home/home"
import Layout from './components/layouts/layout/layout';
import MisFiguritas from "./views/public/mis-figuritas/mis-figuritas.jsx";


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
        path: '/mis-figuritas/nueva-repetida',
        element: <h1>Hola repetida</h1>
    },
    {
        path: '/mis-figuritas/nueva-faltante',
        element: <h1>Hola Faltante</h1>
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