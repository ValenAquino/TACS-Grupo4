import {useState} from "react";
import SectionCard from "../../../components/ui/section-card/section-card.jsx";
import AutocompleteInput from "../../../components/ui/autocomplete-input/autocomplete-input.jsx";
import Button from "../../../components/ui/button/button.jsx";
import {buscarFiguritas} from "../../../services/figuritaService.js";
import {agregarFaltante} from "../../../services/coleccionService.js";

const NuevaFaltante = () => {
    const [figurita, setFigurita] = useState(undefined);
    const [numero, setNumero]   = useState('');
    const [jugador, setJugador] = useState('');

    const colId = "2"

    const handleSelect = (fig) => {
        setFigurita(fig);
        setJugador(fig.jugador);
        setNumero(fig.id);
    };

    const buscarPorJugador = async (texto) => {
        return await buscarFiguritas({ jugador: texto });
    };

    const buscarPorNumero = async (texto) => {
        const resultado = await buscarFiguritas({ id: texto });
        handleSelect(resultado[0]);
    };

    return (
        <div className="d-flex flex-column gap-3">

            <div
                className="d-flex flex-column align-items-center justify-content-center rounded-3 py-4"
                style={{ backgroundColor: '#f8f9fa', border: '1.5px dashed #ced4da', minHeight: '140px' }}
            >
                {figurita ? (
                    <>
                        <p className="mb-0 fw-semibold" style={{ fontSize: '1rem' }}>
                            {figurita.jugador}
                        </p>
                        <p className="mb-0 text-muted" style={{ fontSize: '0.8rem' }}>
                            {figurita.seleccion} · #{figurita.numero}
                        </p>
                    </>
                ) : (
                    <>
                        <span style={{ fontSize: '2rem' }}>🔍</span>
                        <p className="mb-0 text-muted mt-2" style={{ fontSize: '0.85rem' }}>
                            Buscá una figurita para ver la vista previa
                        </p>
                    </>
                )}
            </div>

            <SectionCard>
                <SectionCard.Section>
                    <p className="mb-3 fw-semibold text-uppercase" style={{ fontSize: '0.75rem', letterSpacing: '0.05em' }}>
                        Buscar figurita
                    </p>

                    <div className="mb-3 d-flex align-items-center gap-3">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="Ej: ARG-10"
                            value={numero}
                            onChange={(e) => {
                                setNumero(e.target.value);
                                setFigurita(null);
                        }}/>
                        <Button label={"Buscar"} onClick={() => buscarPorNumero(numero)}/>

                    </div>

                    <AutocompleteInput
                        label="Nombre del jugador"
                        placeholder="Ej: Lionel Messi"
                        value={jugador}
                        onChange={(val) => { setJugador(val); setFigurita(null); }}
                        onSelect={handleSelect}
                        onSearch={buscarPorJugador}
                        debounceMs={300}
                        getLabel={(fig) => fig.jugador}
                    />
                </SectionCard.Section>
            </SectionCard>

            <Button
                label="Publicar faltante ↗"
                disabled={!figurita}
                onClick={() => {
                    agregarFaltante(figurita)
                    alert("Se guardo la faltante")
                    setNumero('')
                    setJugador('')
                    setFigurita(undefined)
                }}
            />

        </div>
    );
}

export default NuevaFaltante;