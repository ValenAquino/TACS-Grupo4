import PerfilSimple from "../../../components/ui/perfil-simple/perfil-simple.jsx";
import FiguritaCard from "./figurita-card.jsx";

const OfertaCard = ({ propuesta, tipo = "RECIBIDA" }) => {
    const esRecibida = tipo === "RECIBIDA";

    return (
        <div
            className="rounded-3 p-3"
            style={{
                backgroundColor: 'rgba(211, 211, 211, 0.15)',
                border: '1.5px solid rgba(211, 211, 211, 0.35)',
            }}
        >
            <div className="row align-items-start g-3">

                {/* IZQUIERDA */}
                <div className="col">
                    <PerfilSimple perfil={esRecibida ? propuesta.autor : propuesta.autor} />
                    <small className="text-uppercase text-muted fw-semibold d-block mt-2 mb-2" style={{ fontSize: '0.7rem', letterSpacing: '0.05em' }}>
                        {esRecibida ? "Vos recibís" : "Vos ofrecés"}
                    </small>
                    <div className="d-flex flex-column gap-2">
                        {(propuesta.figuritas_ofrecidas || []).filter(Boolean).map((fig, i) => (
                            <FiguritaCard key={fig.id ?? i} figurita={fig} />
                        ))}
                    </div>
                </div>

                {/* SEPARADOR */}
                <div className="col-auto d-flex flex-column align-items-center justify-content-center" style={{ paddingTop: '2.5rem' }}>
                    <i className="ti ti-arrows-exchange" style={{ fontSize: '1.4rem', color: 'var(--color-text-secondary)' }} aria-hidden="true" />
                </div>

                {/* DERECHA */}
                <div className="col">
                    <PerfilSimple perfil={esRecibida ? propuesta.destinatario : propuesta.destinatario} />
                    <small className="text-uppercase text-muted fw-semibold d-block mt-2 mb-2" style={{ fontSize: '0.7rem', letterSpacing: '0.05em' }}>
                        {esRecibida ? "Vos entregás" : "Vos recibís"}
                    </small>
                    <div className="d-flex flex-column gap-2">
                        {[propuesta.figurita_buscada].filter(Boolean).map((fig, i) => (
                            <FiguritaCard key={fig.id ?? i} figurita={fig} />
                        ))}
                    </div>
                </div>

            </div>
        </div>
    );
};

export default OfertaCard;