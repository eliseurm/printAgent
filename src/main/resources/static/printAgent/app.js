const api='/api/v1';
const $=id=>document.getElementById(id);
async function get(path,options){const r=await fetch(api+path,options);if(!r.ok)throw new Error(`Falha HTTP ${r.status}`);const body=await r.json();if(!body.sucesso)throw new Error(body.mensagem);return body.dados}
function rows(items){if(!items.length)return '<p class="meta">Nenhum trabalho encontrado.</p>';return `<table><thead><tr><th>Nome</th><th>Impressora</th><th>Tipo</th><th>Status</th><th>Criação</th></tr></thead><tbody>${items.map(x=>`<tr><td>${safe(x.nome)}</td><td>${safe(x.impressora)}</td><td>${safe(x.tipoConteudo)}</td><td>${safe(x.status)}</td><td>${new Date(x.dataCriacao).toLocaleString('pt-BR')}</td></tr>`).join('')}</tbody></table>`}
const safe=v=>String(v??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
async function status(){const x=await get('/status');$('estado').textContent=x.status;$('versao').textContent=x.versao;$('fila').textContent=x.trabalhosNaFila;$('totalImpressoras').textContent=x.impressorasEncontradas;$('sistema').textContent=x.sistemaOperacional}
async function impressoras(){const xs=await get('/impressoras');$('listaImpressoras').innerHTML=xs.length?xs.map(x=>`<article class="card"><strong>${safe(x.nome)}</strong><span class="${x.status==='READY'?'ok':'bad'}">${safe(x.status)}</span><div class="meta">${x.padrao?'Impressora padrão':'Impressora instalada'}</div></article>`).join(''):'<p class="meta">Nenhuma impressora encontrada.</p>'}
async function trabalhos(){$('listaTrabalhos').innerHTML=rows(await get('/trabalhos?limite=100'))}
async function historico(){$('listaHistorico').innerHTML=rows(await get('/historico?limite=100'))}
async function logs(){const x=await get('/logs?linhas=300');$('conteudoLog').textContent=x.linhas.join('\n')||'Nenhuma linha de log disponível.'}
const actions={impressoras,trabalhos,historico,logs};
async function run(fn){try{$('erro').textContent='';await fn()}catch(e){$('erro').textContent='Não foi possível atualizar: '+e.message}}
document.querySelectorAll('nav button').forEach(b=>b.onclick=()=>{document.querySelectorAll('nav button').forEach(x=>x.classList.remove('active'));document.querySelectorAll('.tab').forEach(x=>x.classList.add('hidden'));b.classList.add('active');$(b.dataset.tab).classList.remove('hidden');if(actions[b.dataset.tab])run(actions[b.dataset.tab])});
document.querySelectorAll('[data-action]').forEach(b=>b.onclick=()=>run(actions[b.dataset.action]));
const printExample={"impressora":{"nome":"ElginL42"},"configuracoesImpressao":{},"trabalho":{"nome":"Teste pelo painel","copias":1,"sincrono":true},"documento":{"tipoConteudo":"application/zpl","codificacao":"utf-8","conteudo":"^XA^FO20,20^A0N,30,30^FDTESTE PRINT AGENT^FS^XZ"}};
const examples={status:{method:'GET',path:'/status',json:{}},impressoras:{method:'GET',path:'/impressoras',json:{}},criarTrabalho:{method:'POST',path:'/trabalhos',json:printExample},listarTrabalhos:{method:'GET',path:'/trabalhos',json:{limite:100}},historico:{method:'GET',path:'/historico',json:{limite:100}},logs:{method:'GET',path:'/logs',json:{linhas:300}},configuracoes:{method:'GET',path:'/configuracoes',json:{}}};
function selectExample(){const x=examples[$('exemploTeste').value];$('rotuloJson').textContent=`JSON para ${x.method} /api/v1${x.path}`;$('jsonTeste').value=JSON.stringify(x.json,null,2);$('resultadoTeste').textContent='';$('respostaTeste').textContent='A resposta da API será exibida aqui.'}
function normalizarConteudoDocumento(texto){
  const inicio=/"conteudo"\s*:\s*"/g.exec(texto);
  if(!inicio)return texto;
  let resultado=texto.slice(0,inicio.index+inicio[0].length),escapado=false,dadosDoCampo=false;
  for(let i=inicio.index+inicio[0].length;i<texto.length;i++){
    const caractere=texto[i];
    if(escapado){resultado+=caractere;escapado=false;continue}
    if(caractere==='\\'){resultado+=caractere;escapado=true;continue}
    if(caractere==='"'){resultado+=texto.slice(i);return resultado}
    if(texto.startsWith('^FD',i)){resultado+='^FD';dadosDoCampo=true;i+=2;continue}
    if(dadosDoCampo&&texto.startsWith('^FS',i)){resultado+='^FS';dadosDoCampo=false;i+=2;continue}
    if(/\s/.test(caractere)){
      if(dadosDoCampo&&caractere===' ')resultado+=caractere;
      continue;
    }
    resultado+=caractere;
  }
  return resultado;
}
$('exemploTeste').onchange=selectExample;
$('imprimir').onclick=async()=>{const out=$('resultadoTeste'),response=$('respostaTeste'),x=examples[$('exemploTeste').value];out.className='';out.textContent='Enviando…';response.textContent='Aguardando resposta…';try{const texto=normalizarConteudoDocumento($('jsonTeste').value);$('jsonTeste').value=texto;const json=JSON.parse(texto);let path=x.path,options={method:x.method,headers:{'Accept':'application/json'}};if(x.method==='GET'){const query=new URLSearchParams(Object.entries(json).filter(([,v])=>v!==null&&v!==''));if(query.size)path+='?'+query}else{options.headers['Content-Type']='application/json';options.body=JSON.stringify(json)}const r=await fetch(api+path,options);const body=await r.json();response.textContent=`HTTP ${r.status}\n\n${JSON.stringify(body,null,2)}`;if(!r.ok||!body.sucesso){const detalhes=(body.erros||[]).map(e=>`${e.codigo}: ${e.descricao}`).join(' | ');throw new Error(detalhes||body.mensagem||`Falha HTTP ${r.status}`)}out.textContent=body.mensagem;if(x.method==='POST'){if(body.dados.status==='ERRO')throw new Error('O trabalho terminou com erro. Consulte a aba Logs.');await Promise.all([status(),trabalhos(),historico()])}}catch(e){out.className='bad';out.textContent='Erro: '+e.message;if(response.textContent==='Aguardando resposta…')response.textContent='A requisição não foi enviada: '+e.message;await logs();document.querySelector('[data-tab="logs"]').classList.add('bad')}};
selectExample();
Promise.all([run(status),run(impressoras)]);setInterval(()=>{if(!document.hidden)run(status)},10000);
