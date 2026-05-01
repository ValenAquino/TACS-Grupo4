import { useState, useRef, useEffect } from 'react';

const AutocompleteInput = ({
       value,
       onChange,
       onSelect,
       onSearch,            // modo async: función que recibe el texto y devuelve items
       debounceMs = 300,
       placeholder = '',
       label = '',
       getLabel = (item) => item,
       disabled = false,
}) => {
    const [suggestions, setSuggestions] = useState([]);
    const [loading, setLoading] = useState(false);
    const [open, setOpen] = useState(false);
    const wrapperRef  = useRef(null);
    const debounceRef = useRef(null);

    // Modo async: llamar onSearch con debounce
    useEffect(() => {
        if (!onSearch) return;
        if (!value) { setSuggestions([]); setOpen(false); return; }

        clearTimeout(debounceRef.current);
        debounceRef.current = setTimeout(async () => {
            setLoading(true);
            try {
                const results = await onSearch(value);
                setSuggestions(results);
                setOpen(true);
            } catch (e) {
                setSuggestions([]);
            } finally {
                setLoading(false);
            }
        }, debounceMs);

        return () => clearTimeout(debounceRef.current);
    }, [value, onSearch, debounceMs]);

    useEffect(() => {
        const handleClickOutside = (e) => {
            if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
                setOpen(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const handleChange = (e) => {
        onChange(e.target.value);
        if (!onSearch) setOpen(true);
    };

    const handleSelect = (item) => {
        onSelect(item);
        setSuggestions([]);
        setOpen(false);
    };

    const showDropdown = open && value && (loading || suggestions.length > 0);

    return (
        <div ref={wrapperRef} className="position-relative">
            {label && (
                <label className="form-label text-muted" style={{ fontSize: '0.85rem' }}>
                    {label}
                </label>
            )}
            <input
                type="text"
                className="form-control"
                placeholder={placeholder}
                value={value}
                onChange={handleChange}
                onFocus={() => suggestions.length > 0 && setOpen(true)}
                disabled={disabled}
            />
            {showDropdown && (
                <ul
                    className="list-group position-absolute w-100 shadow-sm"
                    style={{ zIndex: 100, maxHeight: '200px', overflowY: 'auto' }}
                >
                    {loading ? (
                        <li className="list-group-item text-muted d-flex align-items-center gap-2" style={{ fontSize: '0.85rem' }}>
                            <span className="spinner-border spinner-border-sm" />
                            Buscando...
                        </li>
                    ) : (
                        suggestions.map((item, i) => (
                            <li
                                key={i}
                                className="list-group-item list-group-item-action"
                                style={{ fontSize: '0.85rem', cursor: 'pointer' }}
                                onMouseDown={() => handleSelect(item)}
                            >
                                {getLabel(item)}
                            </li>
                        ))
                    )}
                </ul>
            )}
        </div>
    );
};

export default AutocompleteInput;