import React, { useRef, useState } from 'react'
import './textinput.css'
import TextField from '@mui/material/TextField';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Result from '../result/Result';

const API_ADD_ARGUMENTPAIR = "http://localhost:8080/api/v1/argumentpair/add";
const API_AQUAPLANEIFY = "http://localhost:8080/api/v1/argumentpair/aquaplaneify";

const Textinput = () => {
  const form = useRef();
  const [data, setData] = useState(null);
  const [decision, setDecision] = useState(null);
  const [isReadOnly, setReadOnly] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const submit = (event) => {
    event.preventDefault();
    setReadOnly(true);
    setLoading(true);

    const form = event.target;
    const arg1 = form.elements['arg1'].value;
    const arg2 = form.elements['arg2'].value;
    const claim = form.elements['claim'].value;

    const argumentPair = {arg1, arg2, claim};
    saveArgumentPair(argumentPair);
    aquaplaneify(argumentPair);
  };

  async function saveArgumentPair(argumentPair) {
    return fetch(API_ADD_ARGUMENTPAIR,{
            method: "POST",
            headers: {"Content-type": "application/json"},
            body: JSON.stringify(argumentPair)
    })
    .then((response) => {
        if (!response.ok) {
            throw new Error(
                "HTTP error: Status ${response.status}"
            )
        }
        return response.json();
    })
  }

  function aquaplaneify(argumentPair) {
    return fetch(API_AQUAPLANEIFY,{
        method: "POST",
        headers: {"Content-type": "application/json"},
        body: JSON.stringify(argumentPair)
    })
    .then((response) => {
        if (!response.ok) {
            throw new Error(
                "HTTP error: Status ${response.status}"
            )
        }
        return response.json();
    })
    .then((data) => { 
        setData(data)
        setDecision(data.decision)
        setError(null)
    })
    .catch((err) => { 
        setError(err.message)
        setData(null)
    })
    .finally(() => {
        setLoading(false);
    });
  }

  const reset = (event) => {
    setData(null);
    setError(null);
    setDecision(null);
    setReadOnly(false);
    event.target.reset();
  }


  return (
    <form ref={form} onSubmit={submit} onReset={reset}>
        <div>
            <div className="claim__container">
                <TextField 
                    id="outlined-multiline-flexible" 
                    helperText="Please enter the claim to which the arguments relate."
                    multiline 
                    fullWidth 
                    maxRows={3} 
                    label="Claim" 
                    name='claim' 
                    variant="outlined" 
                    InputProps={
                        { 
                          style: {fontSize: 15},
                          readOnly: isReadOnly
                        }
                    }
                    size='small'
                    required
                />
            </div>
            <div className="arguments__container">
                <div className="argument__container">
                    <h3
                        style={decision === 1 ? { color: "var(--color-primary)" } : null}
                    >Argument 1</h3>
                    <textarea 
                        style={decision === 1 ? { border: "2px solid var(--color-primary)" } : null}
                        readOnly={isReadOnly}
                        name='arg1' 
                        rows="10" 
                        placeholder='Lorem ipsum dolor sit amet, consetetur sadipscing elitr...' 
                        required
                    />
                </div>

                <div className="argument__container">
                    <h3
                        style={decision === 2 ? { color: "var(--color-primary)" } : null}
                    >Argument 2</h3>
                    <textarea
                        style={decision === 2 ? { border: "2px solid var(--color-primary)" } : null}
                        readOnly={isReadOnly}
                        name='arg2' 
                        rows="10" 
                        placeholder='Lorem ipsum dolor sit amet, consetetur sadipscing elitr...' 
                        required
                    />
                </div>
            </div>
            {(function() {
                if (loading) {
                    return <div className='loading__container'>
                        <CircularProgress/>
                    </div>
                }
                if (error) {
                    return <div className='alert__container'>
                        <Alert severity="error">There is a problem fetching the data!</Alert>
                    </div>
                }
                if (data) {
                    return <Result data={data}/>
                } else {
                    return <div className='buttons__container'>
                        <button type='submit' className='btn btn-primary'>AQUAPLANEify</button>
                    </div>
                }
            })()}
        </div>
    </form>
  )
}

export default Textinput