import {useState} from "react";
import SectionCard from "../../components/ui/section-card/section-card.jsx";
import AutocompleteInput from "../../components/ui/autocomplete-input/autocomplete-input.jsx";
import Button from "../../components/ui/button/button.jsx";
import {buscarFiguritas} from "../../services/figuritaService.js";

const NuevaFaltante = () => {
    const [figurita, setFigurita] = useState(null);
    const [numero, setNumero]   = useState('');
    const [jugador, setJugador] = useState('');

    const handleSelect = (fig) => {
        setFigurita(fig);
        setJugador(fig.jugador);
        setNumero(fig.id);
    };

    const buscarPorJugador = async (texto) => {
        return await buscarFiguritas({ jugador: texto });
    };

    const buscarPorNumero = async (texto) => {
        return await buscarFiguritas({ id: texto });
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

                    <div className="mb-3">
                        <AutocompleteInput
                            label="Número de figurita"
                            placeholder="Ej: ARG-10"
                            value={numero}
                            onChange={(val) => { setNumero(val); setFigurita(null); }}
                            onSelect={handleSelect}
                            onSearch={buscarPorNumero}
                            debounceMs={300}
                            getLabel={(fig) => `${fig.id} · ${fig.jugador}`}
                        />
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
                label="Publicar figurita ↗"
                disabled={!figurita}
                onClick={() => console.log('publicar', figurita)}
            />

        </div>
    );
}

export default NuevaFaltante;