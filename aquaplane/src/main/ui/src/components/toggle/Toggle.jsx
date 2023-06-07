import React from 'react'
import './toggle.css'
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import Textinput from '../textinput/Textinput';
import Csvinput from '../csvinput/Csvinput';


const Toggle = () => {
  const [inputType, setInputType] = React.useState('text');

  const handleChange = (event, newInputType) => {
    if (newInputType !== null) {
        setInputType(newInputType);
    }
  };

  return (
    <div className="container">
        <div className="toggle__container">
            <h3>Input</h3>
            <ToggleButtonGroup
                color="primary"
                value={inputType}
                exclusive
                onChange={handleChange}
                aria-label="Platform"
                size='small'
            >
                <ToggleButton value="text">Text</ToggleButton>
                <ToggleButton value="csv">CSV</ToggleButton>
            </ToggleButtonGroup>
        </div>

        {inputType === 'text' ? <Textinput/> : <Csvinput/>}
    </div>
  )
}

export default Toggle