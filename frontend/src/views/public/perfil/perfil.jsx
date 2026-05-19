import { useState, useEffect } from "react";
import {buscarContadores,buscarPerfil,buscarCalificaciones} from "../../../services/perfilService.js";
import useUsuarioActual from "../../../hooks/useUsuarioActual.js";
import {useAuth} from "@/contexts/userContext.jsx";
import Button from "@/components/ui/button/button.jsx";
import confirmModal from "@/components/ui/confirm-modal/confirm-modal.jsx";
import ConfirmModal from "@/components/ui/confirm-modal/confirm-modal.jsx";
import {useToast} from "@/contexts/toastContext.jsx";

const renderStars = (score) => {
    const fullStars = Math.floor(score / 2);
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
    const [nombre, setNombre] = useState("Messi G.");
    const [reviews, setReviews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [stats, setStats] = useState({
        intercambios: 0,
        publicadas: 0,
        faltantes: 0,
        subastas: 0
    });

    const {showToast} = useToast();

    const [showConfirmModal, setShowConfirmModal] = useState(false);

    /* Datos Hardcodeados */
    const { user, cerrarSesion} = useAuth();

    const perfilId = user?.perfil_id

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

          const perfil = await buscarPerfil(perfilId);
          const statsData = await buscarContadores({ perfilId });
          const reviewsData = await buscarCalificaciones(perfilId);

          setNombre(perfil.nombre);
          setReviews(reviewsData);
          setStats(statsData);

        } catch (e) {
          console.error(e);
        } finally {
          setLoading(false);
        }
      };

      cargar();
    }, [perfilId]);

    const promedio = reviews.length > 0
        ? (reviews.reduce((acc, r) => acc + r.puntaje, 0) / reviews.length).toFixed(1)
        : 0;

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
                            <h2 className="mb-0 fw-bold"
                            style={{
                            maxWidth: "750px",
                            whiteSpace: "nowrap",
                            overflow: "hidden",
                            textOverflow: "ellipsis"
                            }}>
                            {nombre}</h2>
                            <p className="mb-1">@messi_g</p>
                            <div>
                                {renderStars(Number(promedio))} ⭐ {promedio} ({reviews.length})
                            </div>
                        </div>
                    </div>

                    <div className="d-flex align-items-center gap-3 ">
                        <button
                            className="btn btn-warning"
                            style={{ padding: "10px 18px" }}
                            onClick={() => setShowModal(true)}
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
                    {[
                         { valor: stats.intercambios, label: "Intercambios" },
                         { valor: stats.publicadas, label: "Publicadas" },
                         { valor: stats.faltantes, label: "Faltantes" },
                         { valor: stats.subastas, label: "Subastas" }
                     ].map((stat, i) => (
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
                                {stat.label}
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

                        {loading ? (
                            <p className="text-muted">Cargando reseñas...</p>
                        ) : reviews.length === 0 ? (
                            <p className="text-muted">Este usuario no tiene reseñas disponibles</p>
                        ) : (
                            reviews.map((r, i) => (
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
                                        <div>{renderStars(r.puntaje)} {r.puntaje}/10</div>
                                        <p className="mb-0 text-muted" style={{ fontSize: "0.9rem" }}>
                                            "{r.comentario}"
                                        </p>
                                    </div>
                                </div>
                            ))
                        )}

                    </div>
                </div>
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
                            value={nombre}
                            onChange={(e) => setNombre(e.target.value)}
                        />

                        <label className="text-muted">@messi_g</label>

                        <button
                            className="btn mt-3 w-100"
                            style={{ backgroundColor: "#175A2D", color: "white" }}
                            onClick={() => setShowModal(false)}
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