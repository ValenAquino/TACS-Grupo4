import { useState, useEffect } from "react";
import {buscarContadores,buscarPerfil,buscarCalificaciones,editarPerfil} from "../../../services/perfilService.js";
import {useAuth} from "@/contexts/userContext.jsx";
import Button from "@/components/ui/button/button.jsx";
import ConfirmModal from "@/components/ui/confirm-modal/confirm-modal.jsx";
import {useToast} from "@/contexts/toastContext.jsx";
import Paginacion from "@/components/ui/paginacion/paginacion.jsx";
import {useError} from "@/contexts/errorContext.jsx";
import {truncarADosDecimales} from "@/utils/estandarizar.js";

const renderStars = (score) => {
    const fullStars = Math.floor(score);
    const emptyStars = 5 - fullStars;

    return (
        <>
            {"★".repeat(fullStars)}
            {"☆".repeat(emptyStars)}
        </>
    );
};

const Perfil = () => {

    const [showModal, setShowModal] = useState(false);
    const [nombreEditando, setNombreEditando] = useState("");
    const [perfil, setPerfil] = useState({});
    const [reviews, setReviews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [loadingNotificaciones, setLoadingNotificaciones] = useState(false);
    const [stats, setStats] = useState([]);
    const [pagina, setPagina] = useState(1);
    const [filtros, setFiltros] = useState({});

    const {handleError} = useError();
    const {showToast} = useToast();

    const [showConfirmModal, setShowConfirmModal] = useState(false);

    const {user ,cerrarSesion} = useAuth();

    const perfilId = user.perfil_id;

    const manejarCierreDeSesion = () => {
        try {
            cerrarSesion()
            showToast("Cierre de sesion exitoso", "success");
        } catch (error) {
            showToast("Error al cerra la sesion", "error");
        }
    }

    useEffect(() => {
      const cargar = async () => {
        try {
          setLoading(true);

          const perfil = await buscarPerfil();
          const statsData = await buscarContadores();

          setPerfil(perfil);
          setStats(statsData);

        } catch (error) {
          handleError(error, () => {});
        } finally {
          setLoading(false);
        }
      };

      cargar();
    }, []);

    useEffect(() => {
        const cargarCalificaciones = async () => {
            try {
                setLoadingNotificaciones(true);

                const calificacionesApi = await buscarCalificaciones({
                    ...filtros,
                    pagina,
                    limite: 10,
                });

                setReviews(calificacionesApi);
            } catch (error) {
                handleError(error, () => {})
            } finally {
                setLoadingNotificaciones(false);
            }
        };

        cargarCalificaciones();
    }, [filtros, pagina]);

    const promedio = truncarADosDecimales(perfil.calificacion_media)

    const abrirModalEdicion = () => {
        setNombreEditando(perfil.nombre);
        setShowModal(true);
    };

    const guardarCambios = async () => {
        try {
            await editarPerfil({ nombre: nombreEditando });
            setPerfil((prev) => ({ ...prev, nombre: nombreEditando }));
            setShowModal(false);
            showToast("Perfil actualizado correctamente", "success");
        } catch (error) {
            showToast("Error al guardar los cambios", "error");
        }
    };

    return (
        <div className="d-flex flex-column">

            {/* HEADER */}
            <div style={{
                width: "100vw",
                position: "relative",
                left: "50%",
                marginLeft: "-50vw",
                backgroundColor: "#175A2D",
                marginTop: "-32px"
            }}>
                <div
                    className="mx-auto d-flex justify-content-between align-items-center px-4 py-4"
                    style={{ maxWidth: "1100px" }}
                >

                    <div className="d-flex align-items-center gap-3 text-white">

                        <div style={{
                            width: "90px",
                            height: "90px",
                            borderRadius: "50%",
                            backgroundColor: "#3a7d5d",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            fontSize: "26px",
                            border: "4px solid #629176"
                        }}>
                            MG
                        </div>

                        <div>
                            {loading
                                ? <h2>Cargando datos...</h2>
                                : (
                                    <>
                                        <h2 className="mb-0 fw-bold"
                                            style={{
                                                maxWidth: "750px",
                                                whiteSpace: "nowrap",
                                                overflow: "hidden",
                                                textOverflow: "ellipsis"
                                            }}>
                                            {perfil?.nombre}</h2>
                                        <div>
                                            {renderStars(Number(promedio))} ⭐ {promedio} ({reviews.cantidad_de_elementos})
                                        </div>
                                    </>
                                )
                            }
                        </div>
                    </div>

                    <div className="d-flex align-items-center gap-3 ">
                        <button
                            className="btn btn-warning"
                            style={{ padding: "10px 18px" }}
                            onClick={abrirModalEdicion}
                        >
                            Editar perfil
                        </button>

                        {perfilId != null && <Button
                            label={"Cerrar sesion"}
                            onClick={() => setShowConfirmModal(true)}
                        />}
                    </div>


                </div>
            </div>

            {/* STATS */}
            <div style={{
                backgroundColor: "#0f3d1f",
                width: "100vw",
                position: "relative",
                left: "50%",
                marginLeft: "-50vw"
            }}>
                <div className="mx-auto d-flex text-center text-white" style={{ maxWidth: "60%" }}>
                    {stats.map((stat, i) => (
                        <div
                            key={i}
                            className="flex-fill py-3"
                            style={{
                                borderRight: i !== 3 ? "1px solid rgba(255,255,255,0.2)" : "none"
                            }}
                        >
                            <div className="fw-bold" style={{ color: "#FFC107", fontSize: "2rem" }}>
                                {stat.valor}
                            </div>
                            <div style={{ fontSize: "1rem", marginTop: "2px" }}>
                                {stat.nombre}
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            {/* CONTENIDO */}
            <div className="mx-auto mt-4 px-3" style={{ maxWidth: "1100px", width: "100%" }}>
                <div className="mb-4">
                    <h5 className="mb-3 fw-bold">Últimas calificaciones</h5>

                    <div className="d-flex flex-column gap-3">

                        {loadingNotificaciones ? (
                            <p className="text-muted">Cargando reseñas...</p>
                        ) : reviews.contenido?.length === 0 ? (
                            <p className="text-muted">Este usuario no tiene reseñas disponibles</p>
                        ) : (
                            reviews.contenido?.map((r, i) => (
                                <div
                                    key={i}
                                    className="p-3 bg-white rounded shadow-sm d-flex align-items-center gap-3"
                                >
                                    <div style={{
                                        width: "50px",
                                        height: "50px",
                                        borderRadius: "50%",
                                        backgroundColor: "#ccc",
                                        display: "flex",
                                        alignItems: "center",
                                        justifyContent: "center",
                                        fontSize: "16px",
                                        fontWeight: "bold"
                                    }}>
                                        {r.iniciales}
                                    </div>

                                    <div className="flex-grow-1">
                                        <strong>{r.nombre}</strong>
                                        <div>{renderStars(r.valor)} {r.valor}/5</div>
                                        <p className="mb-0 text-muted" style={{ fontSize: "0.9rem" }}>
                                            "{r.descripcion}"
                                        </p>
                                    </div>
                                </div>
                            ))
                        )}

                    </div>
                </div>
                <Paginacion
                    page={pagina}
                    totalPages={reviews.cantidad_de_paginas ?? 1}
                    onChange={setPagina}/>
            </div>

            {/* MODAL (sin cambios) */}
            {showModal && (
                <div onClick={() => setShowModal(false)} style={{
                    position: "fixed",
                    top: 0,
                    left: 0,
                    width: "100vw",
                    height: "100vh",
                    backgroundColor: "rgba(0,0,0,0.3)",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    zIndex: 999
                }}>
                    <div
                        onClick={(e) => e.stopPropagation()}
                        style={{
                            backgroundColor: "white",
                            padding: "20px",
                            borderRadius: "12px",
                            width: "400px",
                            position: "relative"
                        }}
                    >
                        <button
                            onClick={() => setShowModal(false)}
                            style={{
                                position: "absolute",
                                top: "10px",
                                right: "10px",
                                border: "none",
                                background: "transparent",
                                fontSize: "18px"
                            }}
                        >
                            ✖
                        </button>

                        <h5 className="mb-3">Editar perfil</h5>

                        <label>Nombre</label>
                        <input
                            className="form-control mb-3"
                            value={nombreEditando}
                            onChange={(e) => setNombreEditando(e.target.value)}
                        />

                        <button
                            className="btn mt-3 w-100"
                            style={{ backgroundColor: "#175A2D", color: "white" }}
                            onClick={guardarCambios}
                        >
                            Guardar cambios
                        </button>
                    </div>
                </div>
            )}

            <ConfirmModal
                show={showConfirmModal}
                titulo={"Esta seguro que quiere cerrar su sesion?"}
                labelConfirmar={"Aceptar"}
                onConfirmar={manejarCierreDeSesion}
                onCancelar={() => setShowConfirmModal(false)}
            />
        </div>
    );
};

export default Perfil;