import { Route, Routes } from 'react-router-dom'
import Layout from './components/layouts/layout/layout'
import MisFiguritas from './views/public/mis-figuritas/mis-figuritas.jsx'
import NuevaFaltante from './views/public/nueva-faltante/nueva-faltante.jsx'
import NuevaRepetida from './views/public/nueva-repetida/nueva-repetida.jsx'
import Subastas from './views/public/subastas/subastas.jsx'
import CrearSubasta from './views/public/crear-subasta/crear-subasta.jsx'
import Sugerencias from './views/public/sugerencias/sugerencias.jsx'
import VerSubasta from './views/public/ver-subasta/ver-subasta.jsx'
import Explorar from './views/public/explorar/explorar.jsx'
import Perfil from "./views/public/perfil/perfil.jsx";
import Intercambios from "./views/public/intercambios/intercambios.jsx";
import Login from "@/views/public/login/login.jsx";
import Registrar from "@/views/public/registrar/registrar.jsx";

const publics = [
  {
    path: '/',
    element: <Explorar />,
  },
  {
    path: '/mis-figuritas',
    element: <MisFiguritas />,
  },
  {
    path: '/mis-figuritas/nueva-faltante',
    element: <NuevaFaltante />,
  },
  {
    path: '/explorar',
    element: <Explorar />,
  },
  {
    path: '/mis-figuritas/nueva-repetida',
    element: <NuevaRepetida />,
  },
  {
    path: '/subastas',
    element: <Subastas />,
  },
  {
    path: '/subastas/crear',
    element: <CrearSubasta />,
  },
  {
      path: '/sugerencias',
      element: <Sugerencias />
  },
  {
      path: '/perfil',
      element: <Perfil />
  },
  {
      path: '/intercambios',
      element: <Intercambios />
  },
  {
      path: '/subastas/:subId',
      element: <VerSubasta />
  },
  {
      path: '/login',
      element: <Login />,
  },
  {
    path: '/registrar',
    element: <Registrar />,
  }
];

const privates = []

const AppRoutes = () => {
  return (
    <Routes>
      <Route element={<Layout />}>
        {publics.map((route) => (
          <Route key={route.path} path={route.path} element={route.element} />
        ))}
        {privates.map((route) => (
          <Route key={route.path} path={route.path} element={route.element} />
        ))}
      </Route>
      {/* <Route path="*" element={<NotFound />} /> */}
    </Routes>
  )
}

export default AppRoutes